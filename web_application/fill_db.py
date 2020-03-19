import os
from django.utils import timezone

import django
import time

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "supermarket_manager.settings")
django.setup()

import random

from webapp.models import Company, Item, SupermarketBranch, Aisle, \
    BranchInventory, ShoppingCart, Transaction, TransactionData
from django.contrib.auth.models import User


#  Taken from http://www.geeksforgeeks.org/print-colors-python-terminal/
def prRed(skk): print("\033[91m{}\033[00m".format(skk))


# TODO add something to ensure that new DB rows are only shown when they are successfully inserted

# These dictionaries should be included in the maisample_usern project to validate accepted categories of item
supermarket_companies = {
    "aldi": "Aldi",
    "coles": "Coles",
    "woolworths": "Woolworths"
}

item_categories = {
    "fruit": "fruit",
    "vegetable": "vegetable",
    "confectionery": "confectionery",
    "beverage": "beverage"
}

DEFAULT_QUANTITY = 10
DEFAULT_PRICE = 5.00

DEFAULT_MANAGER_PASSWORD = 'pass1234'  # All managers need to change their password on first log in.

DEFAULT_BRANCH_ADDRESS = "123_default_addr"
DEFAULT_BRANCH_TELEPHONE = "123456789"



def add_companies():
    companies = list()
    aldi = Company(name=supermarket_companies['aldi'])
    coles = Company(name=supermarket_companies['coles'])
    woolworths = Company(name=supermarket_companies['woolworths'])

    companies.append(aldi)
    companies.append(coles)
    companies.append(woolworths)

    print("Companies added to the database:")
    for c in companies:
        try:
            res = c.save()
            print(res)
        except django.db.utils.IntegrityError as err:

            if "UNIQUE constraint failed" in err.args[0]:
                pass
            else:
                # print(err.args[0])
                raise
        else:
            print("\t{}".format(c))
    print()


def add_items():
    items = list()
    # TODO make the barcodes the actual output of barcode scanner
    # Add fruits
    apple = Item(barcode="apple", name="apple",
                 category=item_categories["fruit"])
    orange = Item(barcode="orange", name="orange",
                  category=item_categories["fruit"])
    banana = Item(barcode="banana", name="banana",
                  category=item_categories["fruit"])
    mango = Item(barcode="mango", name="mango",
                 category=item_categories["fruit"])

    # Add vegtables
    lettuce = Item(barcode="lettuce", name="lettuce",
                   category=item_categories["vegetable"])
    potato = Item(barcode="potato", name="potato",
                  category=item_categories["vegetable"])
    carrot = Item(barcode="carrot", name="carrot",
                  category=item_categories["vegetable"])
    tomato = Item(barcode="tomato", name="tomato",
                  category=item_categories["vegetable"])

    # Add confectionary
    chocolate = Item(barcode="chocolate", name="chocolate",
                     category=item_categories["confectionery"])
    gum = Item(barcode="gum", name="gum",
               category=item_categories["confectionery"])
    licorice = Item(barcode="licorice", name="licorice",
                    category=item_categories["confectionery"])
    lolly_pop = Item(barcode="lolly_pop", name="lolly_pop",
                     category=item_categories["confectionery"])

    # Add beverages
    spring_water = Item(barcode="spring_water", name="spring_water",
                        category=item_categories["beverage"])
    mineral_water = Item(barcode="mineral_water", name="mineral_water",
                         category=item_categories["beverage"])
    lemonade = Item(barcode="lemonade", name="lemonade",
                    category=item_categories["beverage"])

    # Add the items into a list to perform a bulk insert
    # Add fruits
    items.append(apple)
    items.append(orange)
    items.append(banana)
    items.append(mango)

    # Add vegtables
    items.append(lettuce)
    items.append(potato)
    items.append(carrot)
    items.append(tomato)

    # Add confectionary
    items.append(chocolate)
    items.append(gum)
    items.append(licorice)
    items.append(lolly_pop)

    # Add beverages
    items.append(spring_water)
    items.append(mineral_water)
    items.append(lemonade)

    print("Items added to the database:")
    for i in items:
        try:
            i.save()
        except Exception as err:
            raise

        else:
            print("\t{}".format(i))
    print()


def add_managers():
    coles_randwick_manager = "coles_randwick_manager"
    aldi_coogee_manager = "aldi_coogee_manager"
    woolworths_bondi_manager = "woolworths_bondi_manager"

    prRed("Adding manager user: " + str(coles_randwick_manager))
    save_user(coles_randwick_manager, coles_randwick_manager.split('_')[0] + '-' + coles_randwick_manager.split('_')[1],
              coles_randwick_manager.split('_')[2], DEFAULT_MANAGER_PASSWORD)

    prRed("Adding manager user: " + str(aldi_coogee_manager))
    save_user(aldi_coogee_manager, aldi_coogee_manager.split('_')[0] + '-' + aldi_coogee_manager.split('_')[1],
              aldi_coogee_manager.split('_')[2], DEFAULT_MANAGER_PASSWORD)

    prRed("Adding manager user: " + str(woolworths_bondi_manager))
    save_user(woolworths_bondi_manager,
              woolworths_bondi_manager.split('_')[0] + '-' + woolworths_bondi_manager.split('_')[1],
              woolworths_bondi_manager.split('_')[2], DEFAULT_MANAGER_PASSWORD)


def add_sample_user():
    sample_user = "sample_user"
    prRed("Adding sample user: " + str(sample_user))
    save_user(sample_user, "s_first-name", "s_first-name", DEFAULT_MANAGER_PASSWORD)

    user = User.objects.all().filter(username=sample_user)[0]
    user.first_name = 'Test_First_Name'
    user.last_name = 'Test_Last_Name'
    user.save()

    add_transaction(user)
    add_transaction(user)

    # cart = ShoppingCart(user=user)
    # cart.save()

def add_transaction(user):
    print("Will use items from Coles Randwick")
    avaliable_items = BranchInventory.objects.all().filter(supermarketBranch__company__name='Aldi', supermarketBranch__branchName="Coogee")
    print("The avaliable items are:")
    print(avaliable_items)

    item_list = list()
    total_price = 0
    for x in range(0,5):
#         TODO randomise this
        quantity = random.randint(0,5)
        print("\t\tAdding with a quantity: {}".format(quantity))
        item_list.append((avaliable_items[x].item, quantity, avaliable_items[x].price))
        total_price += (avaliable_items[x].price * quantity)

    print(item_list)
    print(total_price)

    test_transaction = Transaction(supermarketBranch=avaliable_items[0].supermarketBranch, user=user, total_price=total_price, time=timezone.now())
    test_transaction.save()

    for tup in item_list:
        transaction_data = TransactionData(transaction_id=test_transaction.id, item=tup[0], quantity=tup[1], price=tup[2])
        transaction_data.save()

    print("Completed")




def save_user(uname, first_name, last_name, pword):
    try:
        User.objects.create_user(username=uname,
                                 first_name=first_name,
                                 last_name=last_name,
                                 password=DEFAULT_MANAGER_PASSWORD)
    # Is this a bug in Django ^^ allowing no password
    except django.db.utils.IntegrityError as err:

        if "UNIQUE constraint failed" in err.args[0]:
            pass
        else:
            raise


def add_supermarket_branches():
    branches = list()

    aldi_coogee = SupermarketBranch(branchName="Coogee",
                                    address=DEFAULT_BRANCH_ADDRESS,
                                    telephone=DEFAULT_BRANCH_TELEPHONE + 'A',
                                    company=Company.objects.filter(
                                        name__exact=supermarket_companies[
                                            'aldi'])[0], manager=
                                    User.objects.filter(
                                        username__exact='aldi_coogee_manager').first())
    coles_randwick = SupermarketBranch(branchName="Randwick",
                                       address=DEFAULT_BRANCH_ADDRESS,
                                       telephone=DEFAULT_BRANCH_TELEPHONE + 'B',
                                       company=Company.objects.filter(
                                           name__exact=supermarket_companies[
                                               'coles'])[0], manager=
                                       User.objects.filter(
                                           username__exact='coles_randwick_manager').first())
    woolies_bondi = SupermarketBranch(branchName="Bondi",
                                      address=DEFAULT_BRANCH_ADDRESS,
                                      telephone=DEFAULT_BRANCH_TELEPHONE + 'C',
                                      company=Company.objects.filter(
                                          name__exact=supermarket_companies[
                                              'woolworths'])[0], manager=
                                      User.objects.filter(
                                          username__exact='woolworths_bondi_manager').first())

    branches.append(aldi_coogee)
    branches.append(coles_randwick)
    branches.append(woolies_bondi)
    print("Branches added to the database:")
    for b in branches:
        try:
            print("Adding branch: {}-{}".format(b.company, b.branchName))
            b.save()
        except django.db.utils.IntegrityError as err:

            if "UNIQUE constraint failed" in err.args[0]:
                print("\tUNIQUE CONSTRAINT FAILED")
                pass
            else:
                # print(err.args[0])
                raise
        else:
            print("\t{}".format(b))
    print("There are {} branches in the system.".format(
        len(SupermarketBranch.objects.all())))
    print()


def add_supermarket_items():
    branches = SupermarketBranch.objects.all()
    items = Item.objects.all()
    ailse_num = int(0)
    print("Adding items to branch:")
    for b in branches:
        print(b)
        for i in items:
            print("\t{}".format(i))
            # print(ailse_num )
            ailse = Aisle(aisleNum=ailse_num, name=i.category,
                          description="This is a blank description",
                          supermarketBranch=b)
            ailse.save()
            ailse_num += 1

            inventoryItem = BranchInventory(item=i, supermarketBranch=b,
                                            quantity=DEFAULT_QUANTITY,
                                            price=random.uniform(0, 9),
                                            aisle=ailse)
            inventoryItem.save()


def main():
    add_companies()
    add_items()
    add_managers()

    add_supermarket_branches()
    add_supermarket_items()

    add_sample_user()


if __name__ == "__main__":
    main()


