from django.http import HttpResponse
from django.shortcuts import render
from django.http.request import HttpRequest
from django.core import serializers
import json

from .models import User


def index(request):
    users = User.objects.all()
    users_dict = [dict([(field.name, user.__getattribute__(field.name)) for field in user._meta.fields]) for user in users]
    users_json = json.dumps(users_dict, indent=2, ensure_ascii=False)
    return HttpResponse(users_json, content_type="application/json")

def register(request: HttpRequest):
    user = User()
    user.first_name = request.GET["first_name"]
    user.last_name = request.GET["last_name"]
    user.phone_number = request.GET["phone_number"]
    user.language = request.GET["language"]
    user.email = request.GET["email"]
    user.country = request.GET["country"]

    user.broker_balance = 0
    user.save()
    return HttpResponse("Ok")
