from django.db import models

class AppUser(models.Model):
    uid = models.CharField(max_length=100, unique=True)
    username = models.CharField(max_length=100)
    first_name = models.CharField(max_length=100)
    last_name = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    password = models.CharField(max_length=100)
    avatar = models.CharField(max_length=255, default="")
    saved_targets = models.JSONField(default=list)

    def __str__(self):
        return self.email