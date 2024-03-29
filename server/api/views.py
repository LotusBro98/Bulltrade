import sys

from django.conf import settings
from django.db.models import Count, Value, Q
from django.db.models.functions import Concat, Lower
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render
from django.http.request import HttpRequest
from django.core import serializers
import json
import base64

from django.views.decorators.csrf import csrf_exempt
from django.contrib.staticfiles import finders
from django.templatetags.static import static

from .models import User, Chat, Message


def index(request):
    return render(request, 'index.html')


def delete(request):
    return render(request, 'delete.html')


def privacy(request):
    return render(request, 'privacy.html')


def all_users(request):
    users = User.objects.all()
    users_list = []
    for user in users:
        user_dict = {}
        for field in user._meta.fields:
            if field.name == "avatar_base64":
                avatar = user.__getattribute__(field.name)
                if avatar == "":
                    user_dict["avatar_base64"] = ""
                else:
                    user_dict["avatar_base64"] = avatar[:100] + " ..."
            else:
                user_dict[field.name] = user.__getattribute__(field.name)
        users_list.append(user_dict)

    users_json = json.dumps(users_list, indent=2, ensure_ascii=False)
    return HttpResponse(users_json, content_type="application/json")

def register(request: HttpRequest):
    user = User()
    user.first_name = request.GET["first_name"]
    user.last_name = request.GET["last_name"]
    user.phone_number = request.GET["phone_number"]
    user.language = request.GET["language"]
    user.email = request.GET["email"]
    user.country = request.GET["country"]
    user.avatar_base64 = ""

    user.broker_balance = 100000
    user.save()
    return HttpResponse(json.dumps({"user_id": user.id}), content_type="application/json")

@csrf_exempt
def set_avatar(request: HttpRequest):
    user_id = int(request.GET["user_id"])
    data = request.POST["data"]

    user = User.objects.get(id=user_id)
    if user is None:
        return HttpResponse("User not found", content_type="text/html", status=404)

    data = base64.b64decode(data)

    filename = "avatars/{}.png".format(user_id)
    filename = finders.find(filename)
    if filename is None:
        filename = finders.find("avatars")
        filename = filename + "/{}.png".format(user_id)

    with open(filename, "wb") as f:
        f.write(data)

    url = static('avatars/{}.png'.format(user_id))
    url = settings.BASE_URL + url

    user.avatar = url
    user.save()
    return HttpResponse("Ok", content_type="text/html")


def get_avatar(request: HttpRequest):
    user_id = int(request.GET["user_id"])

    user = User.objects.get(id=user_id)
    if user is not None:
        if hasattr(user, "avatar"):
            return HttpResponse(user.avatar, content_type="text/html")
        else:
            return HttpResponse("None", content_type="text/html")
    else:
        return HttpResponse("User not found", content_type="text/html", status=404)


def search_users(query: str):
    return User.objects\
        .annotate(full_name_1=Concat('first_name', Value(' '), 'last_name')) \
        .annotate(full_name_2=Concat('last_name', Value(' '), 'first_name')) \
        .filter(Q(full_name_1__contains=query) | Q(full_name_2__contains=query))

def get_dialogue(user_1, user_2, force_create=False):
    if user_1.id == user_2.id:
        chats = Chat.objects.annotate(users_count=Count('users')).filter(users=user_1).filter(users_count=1)
    else:
        chats = Chat.objects.annotate(users_count=Count('users')).filter(users=user_1).filter(users=user_2).filter(users_count=2)

    if len(chats) == 0:
        if not force_create:
            return None

        chat = Chat()
        chat.count = 0
        chat.save()
        chat.users.add(user_1)
        chat.users.add(user_2)
        chat.save()
        return chat

    assert len(chats) == 1
    return chats[0]

def get_users(request: HttpRequest):
    user_id = int(request.GET["user_id"])
    if "query" in request.GET:
        query = request.GET["query"]
    else:
        query = ""

    user0 = User.objects.get(id=user_id)
    if user0 is None:
        return HttpResponse("User not found", content_type="text/html", status=404)

    if query == "":
        chats = Chat.objects.filter(users=user0)
        chats = chats.exclude(hidden_users=user0)
        users = User.objects.filter(chat__in=chats).distinct()
        if user0 in users:
            self_chat = Chat.objects.annotate(users_count=Count('users')).filter(users=user0).filter(users_count=1)
            if len(self_chat) == 0:
                users = [u for u in users if u != user0]
    else:
        users = search_users(query)

    users_list = []
    for user in users:
        chat = get_dialogue(user0, user)
        if chat is None:
            blocked_me = False
            blocked_friend = False
        else:
            blocked_me = user0 in chat.blocked_users.all()
            blocked_friend = user in chat.blocked_users.all()
        user_dict = {}
        for field in user._meta.fields:
            if field.name in ["id", "first_name", "last_name", "avatar"]:
                user_dict[field.name] = user.__getattribute__(field.name)
        user_dict["blocked_me"] = blocked_me
        user_dict["blocked_friend"] = blocked_friend
        users_list.append(user_dict)

    users_json = json.dumps({"users": users_list}, indent=2, ensure_ascii=False)
    return HttpResponse(users_json, content_type="application/json")


def get_messages(request: HttpRequest):
    user_id = int(request.GET["user_id"])
    friend_id = int(request.GET["friend_id"])
    if "from_n" in request.GET:
        from_n = int(request.GET["from_n"])
    else:
        from_n = 0

    user = User.objects.get(id=user_id)
    friend = User.objects.get(id=friend_id)
    if user is None:
        return HttpResponse("User not found", content_type="text/html", status=404)
    if friend is None:
        return HttpResponse("Friend not found", content_type="text/html", status=404)

    chat = get_dialogue(user, friend)
    if chat is None:
        response = {"messages": [], "count": 0}
        return JsonResponse(response, content_type="application/json")

    if from_n < 0:
        messages = Message.objects.filter(chat=chat, seq__gte=chat.count+from_n)
    elif from_n > 0:
        messages = Message.objects.filter(chat=chat, seq__gte=from_n)
    else:
        messages = Message.objects.filter(chat=chat)

    messages = messages.exclude(id_from=friend_id, hidden=True)

    messages = {
        "messages": [
            {
                "from": message.id_from,
                "text": message.text,
                "time": message.send_time.strftime("%H:%M %d.%m.%Y"),
                "seq": message.seq
            }
            for message in messages
        ],
        "count": chat.count
    }
    return HttpResponse(json.dumps(messages, indent=2, ensure_ascii=False), content_type="application/json")


def send_message(request: HttpRequest):
    user_id = int(request.GET["user_id"])
    friend_id = int(request.GET["friend_id"])
    text = request.GET["text"]

    user = User.objects.get(id=user_id)
    friend = User.objects.get(id=friend_id)
    if user is None:
        return HttpResponse("User not found", content_type="text/html", status=404)
    if friend is None:
        return HttpResponse("Friend not found", content_type="text/html", status=404)

    chat = get_dialogue(user, friend, force_create=True)

    blocked = ((friend in chat.blocked_users.all()) or (user in chat.blocked_users.all()))
    if blocked:
        return HttpResponse("User is blocked", content_type="text/html", status=403)

    message = Message()
    message.id_from = user_id
    message.chat = chat
    message.text = text
    message.seq = chat.count
    chat.count = chat.count + 1
    message.save()
    chat.save()

    return HttpResponse("Ok", content_type="text/html")


def delete_message(request: HttpRequest):
    user_id = int(request.GET["user_id"])
    friend_id = int(request.GET["friend_id"])
    seq = int(request.GET["seq"])

    user = User.objects.get(id=user_id)
    friend = User.objects.get(id=friend_id)
    if user is None:
        return HttpResponse("User not found", content_type="text/html", status=404)
    if friend is None:
        return HttpResponse("Friend not found", content_type="text/html", status=404)

    chat = get_dialogue(user, friend, force_create=True)

    message: Message = Message.objects.get(seq=seq, chat=chat)
    if message is not None:
        if message.id_from != user_id:
            message.hidden = True
            message.save()
        else:
            message.delete()

    return HttpResponse("Ok", content_type="text/html")


def set_block(request: HttpRequest):
    user_id = int(request.GET["user_id"])
    friend_id = int(request.GET["friend_id"])
    block = request.GET["block"] == "1"
    hide = request.GET["hide"] == "1" if "hide" in request.GET else False

    user = User.objects.get(id=user_id)
    friend = User.objects.get(id=friend_id)
    if user is None:
        return HttpResponse("User not found", content_type="text/html", status=404)
    if friend is None:
        return HttpResponse("Friend not found", content_type="text/html", status=404)

    chat = get_dialogue(user, friend)
    print(request.GET["block"], file=sys.stderr)
    if block:
        chat.blocked_users.add(user)
    else:
        chat.blocked_users.remove(user)

    if hide:
        chat.hidden_users.add(user)

    chat.save()

    return HttpResponse("Ok", content_type="text/html")
