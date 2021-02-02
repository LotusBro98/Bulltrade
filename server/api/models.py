from django.db import models

class User(models.Model):
    id = models.IntegerField(primary_key=True)
    first_name = models.CharField(max_length=256)
    last_name = models.CharField(max_length=256)
    phone_number = models.CharField(max_length=256)
    language = models.CharField(max_length=256)
    email = models.CharField(max_length=256)
    country = models.CharField(max_length=256)

    broker_balance = models.FloatField()

