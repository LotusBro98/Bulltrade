from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('privacy', views.privacy, name='privacy'),
    path('delete', views.delete, name='delete'),
    path('view_all_users', views.all_users, name='all_users'),
    path('register', views.register, name='register'),
    path('set_avatar', views.set_avatar, name='set_avatar'),
    path('get_avatar', views.get_avatar, name='get_avatar'),
    path('get_users', views.get_users, name='get_users'),
    path('get_messages', views.get_messages, name='get_messages'),
    path('send_message', views.send_message, name='send_message'),
    path('delete_message', views.delete_message, name='delete_message'),
    path('set_block', views.set_block, name='set_block'),
]