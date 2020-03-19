from rest_framework import serializers
from webapp.models import *
from django.utils import timezone


class ItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = Item
        fields = ('barcode', 'name', 'category')


class SupermarketBranchSerializer(serializers.ModelSerializer):
    manager_username = serializers.ReadOnlyField(source='manager.username')
    companyName = serializers.ReadOnlyField(source='company.name')

    class Meta:
        model = SupermarketBranch
        fields = ('id', 'branchName', 'address', 'telephone', 'company', 'companyName', 'manager', 'manager_username')


# this serializer for getting the branch item list ONLY
class BranchInventoryGetSerializer(serializers.ModelSerializer):
    barcode = serializers.ReadOnlyField(source='item.barcode')
    name = serializers.ReadOnlyField(source='item.name')

    class Meta:
        model = BranchInventory
        fields = ('id','barcode', 'name', 'quantity', 'price', 'aisle')


class BranchItemListSerializer(serializers.ModelSerializer):
    managerName = serializers.ReadOnlyField(source='manager.username')
    companyName = serializers.ReadOnlyField(source='company.name')
    items = BranchInventoryGetSerializer(source='branchinventory_set', many=True)

    class Meta:
        model = SupermarketBranch
        fields = ('id', 'items')


class BranchInventorySerializer(serializers.ModelSerializer):
    class Meta:
        model = BranchInventory
        fields = ("id", "item", "supermarketBranch", "quantity", "price", "aisle")


class UserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)
    # supermarket = SupermarketBranchSerializer(source='supermarketbranch_set')

    class Meta:
        model = User
        fields = ('username', 'first_name', 'last_name', 'email', 'password', 'supermarket')

    def create(self, validated_data):
        user = User.objects.create_user(**validated_data)
        return user


class UserAuthenticateSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)
    manager = serializers.PrimaryKeyRelatedField(read_only=True, many=True)

    class Meta:
        model = User
        fields = ('username', 'first_name', 'last_name', 'email', 'password', 'manager')


class TransactionDataSerializer(serializers.ModelSerializer):
    item_name = serializers.ReadOnlyField(source='item.name')

    class Meta:
        model = TransactionData
        fields = ('item', 'item_name', 'quantity', 'price')


class CustomDateTimeField(serializers.DateTimeField):
    def to_representation(self, value):
        tz = timezone.get_default_timezone()
        # timezone.localtime() defaults to the current tz, you only
        # need the `tz` arg if the current tz != default tz
        value = timezone.localtime(value, timezone=tz)
        # py3 notation below, for py2 do:
        # return super(CustomDateTimeField, self).to_representation(value)
        return super().to_representation(value)


class TransactionSerializer(serializers.ModelSerializer):
    transactionData = TransactionDataSerializer(source='transactiondata_set', many=True)
    time = CustomDateTimeField('%Y-%m-%d %H:%M:%S')

    class Meta:
        model = Transaction
        fields = ('id','user','supermarketBranch', 'total_price', 'time', 'transactionData')


# class ShoppingCartListSerializer(serializers.ModelSerializer):
#     items = ShoppingCartSerializer
#
#     class Meta:
#         model = ShoppingCart
#
#


class ShoppingCartSerializer(serializers.ModelSerializer):
    item_name = serializers.PrimaryKeyRelatedField(source='item.item.name', read_only=True)
    barcode = serializers.PrimaryKeyRelatedField(source='item.item.barcode', read_only=True)
    price = serializers.PrimaryKeyRelatedField(source='item.price', read_only=True)

    class Meta:
        model = ShoppingCart
        fields = ('user', 'branch', 'item', 'item_name', 'barcode', 'quantity', 'price')


# class ShoppingCartSerializer(serializers.ModelSerializer):
#     class Meta:
#         model = ShoppingCart
#         fields = ('user', 'branch', 'item', 'quantity')


    # def update(self, instance, validated_data):
    #     instance.username = validated_data.get('username', instance.username)
    #     instance.first_name = validated_data.get('first_name', instance.first_name)
    #     instance.last_name = validated_data.get('last_name', instance.last_name)
    #     instance.email = validated_data.get('email', instance.email)
    #     return instance
