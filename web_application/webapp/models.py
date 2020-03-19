from django.db import models
from django.contrib.auth.models import User

"""
A company has supermarket branches, and supermarket branches has aisles.
A supermarket branch has items related to it, as well as the transactions related to it.
Both of the above relations are many to many relations.
"""
# TODO need to add in something to associate a user to a certain branch

# Create your models here.
class Company(models.Model):
    name = models.CharField(max_length=100, unique=True)

    def __str__(self):
        return self.name


class Item(models.Model):
    # use barcode as the primary key (I think this makes sense? haha)
    barcode = models.CharField(max_length=256, primary_key=True)
    name = models.CharField(max_length=200)  # we might have same products of different sizes, hence not unique names
    category = models.CharField(max_length=100)

    def __str__(self):
        return self.name


class SupermarketBranch(models.Model):
    # What is the primary key of a branch ??
    branchName = models.CharField(max_length=100)
    address = models.TextField(null=True)
    telephone = models.CharField(max_length=30, unique=True)
    company = models.ForeignKey(Company, on_delete=models.CASCADE)
    # assuming a branch only has 1 manager?
    manager = models.ForeignKey(User, on_delete=models.CASCADE, related_name='manager')
    items = models.ManyToManyField(Item, through="webapp.BranchInventory")
    # TODO should this be transactions or transaction data
    transactions = models.ManyToManyField(User, through="webapp.Transaction")

    def __str__(self):
        return "{}".format(self.branchName)


class Aisle(models.Model):
    # Avoid duplicate aisles ?
    aisleNum = models.IntegerField(null=False)
    name = models.CharField(max_length=50)
    description = models.CharField(max_length=100)
    # TODO supermarketBranch shouldnt be repeated in aisle ?
    supermarketBranch = models.ForeignKey(SupermarketBranch, on_delete=models.CASCADE)

    def __str__(self):
        return "Aisle " + str(self.aisleNum) + " of supermarket " + str(self.supermarketBranch)


class BranchInventory(models.Model):
    # TODO avoid duplicates??
    item = models.ForeignKey(Item, on_delete=models.CASCADE)
    # TODO supermarketBranch shouldnt be repeated in aisle ?
    supermarketBranch = models.ForeignKey(SupermarketBranch, on_delete=models.CASCADE)
    quantity = models.IntegerField(default=0)
    price = models.DecimalField(max_digits=7, decimal_places=2)
    aisle = models.ForeignKey(Aisle, on_delete=models.SET_NULL, null=True)
    restockAlert = models.IntegerField(default=5)

    def __str__(self):
        return str(self.item) + " @ " + str(self.supermarketBranch)

class Transaction(models.Model):
    # set null here for if the supermarket branch closes the customer doesn't lose past transactions?
    supermarketBranch = models.ForeignKey(SupermarketBranch, on_delete=models.SET_NULL, null=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    total_price = models.DecimalField(max_digits=7, decimal_places=2)
    time = models.DateTimeField()

    def __str__(self):
        return "Transaction: Branch = " + str(self.supermarketBranch) + " Price: " + str(self.total_price)


class TransactionData(models.Model):
    transaction = models.ForeignKey(Transaction, on_delete=models.CASCADE)
    item = models.ForeignKey(Item, on_delete=models.SET_NULL, null=True)
    quantity = models.IntegerField(default=0)
    price = models.DecimalField(decimal_places=2, max_digits=7, null=True)
    # TODO should we add in a quantity ??
    # TODO How to get the price of each item
    def __str__(self):
        return str(self.transaction.id) + '-' + str(self.item.name)


class ShoppingCart(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    # I imagine items can only be taken from a valid branch inventory, we need to add in something for validating
    # this... i image something like enforcing through having the branch the user selected
    branch = models.ForeignKey(SupermarketBranch, on_delete=models.CASCADE)
    item = models.ForeignKey(BranchInventory, on_delete=models.CASCADE)
    quantity = models.IntegerField(default=0)


class UserMetaData(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    branch = models.ForeignKey(SupermarketBranch, on_delete=models.CASCADE)

    def __str__(self):
        return str(self.branch.branchName)
