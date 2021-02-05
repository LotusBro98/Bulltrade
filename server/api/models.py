from django.db import models


class User(models.Model):
    id = models.AutoField(primary_key=True)
    first_name = models.CharField(max_length=256)
    last_name = models.CharField(max_length=256)
    phone_number = models.CharField(max_length=256)
    language = models.CharField(max_length=256)
    email = models.CharField(max_length=256)
    country = models.CharField(max_length=256)
    avatar = models.TextField(default="", null=True)

    broker_balance = models.FloatField()

    def __str__(self):
        return "%d: %s %s" % (self.id, self.first_name, self.last_name)


class Chat(models.Model):
    users = models.ManyToManyField(User)
    count = models.IntegerField()

    def __str__(self):
        return str(list(self.users.all()))


class Message(models.Model):
    chat = models.ForeignKey(Chat, on_delete=models.CASCADE)
    id_from = models.IntegerField()
    seq = models.IntegerField()
    text = models.TextField()

    def __str__(self):
        return str(self.id_from) + "> " + self.text

