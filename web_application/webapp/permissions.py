from rest_framework import permissions
from webapp.models import *


class IsManager (permissions.BasePermission):
    def has_permission(self, request, view):
        if request.user.is_superuser:
            return True
        try:
            SupermarketBranch.objects.get(manager=request.user)
        except SupermarketBranch.DoesNotExist:
            return False
        return True


class IsCurrentUser (permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return obj.id == request.user.id

