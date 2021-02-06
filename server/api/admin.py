from django.contrib import admin

# Register your models here.
from api.models import User, Chat, Message

admin.site.register(User)
admin.site.register(Chat)
admin.site.register(Message)