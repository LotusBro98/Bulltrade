import sys

from django.conf import settings
from django.http import HttpResponse
from django.shortcuts import render
from django.http.request import HttpRequest
from django.core import serializers
import json
import base64

from django.views.decorators.csrf import csrf_exempt
from django.contrib.staticfiles import finders
from django.templatetags.static import static

from .models import User, Chat


def index(request):
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
        return HttpResponse(user.avatar_base64, content_type="text/html")
    else:
        return HttpResponse("User not found", content_type="text/html", status=404)


def get_users(request: HttpRequest):
    users = User.objects.all()

    users_list = []
    for user in users:
        user_dict = {}
        for field in user._meta.fields:
            if field.name in ["id", "first_name", "last_name", "avatar"]:
                user_dict[field.name] = user.__getattribute__(field.name)
        users_list.append(user_dict)

    users_json = json.dumps({"users": users_list}, indent=2, ensure_ascii=False)
    return HttpResponse(users_json, content_type="application/json")


def get_messages(request: HttpRequest):
    user_id = int(request.GET["user_id"])
    friend_id = int(request.GET["friend_id"])
    last_n = int(request.GET["last_n"])

    user = User.objects.get(id=user_id)
    friend = User.objects.get(id=friend_id)
    if user is None:
        return HttpResponse("User not found", content_type="text/html", status=404)
    if friend is None:
        return HttpResponse("Friend not found", content_type="text/html", status=404)

    chats = Chat.objects.filter(user__in=[user, friend])
    print(chats, file=sys.stderr)

    messages = {"messages": [{"from": friend_id, "text": "Hello"}], "last_seq": 0}
    return HttpResponse(messages, content_type="application/json")
