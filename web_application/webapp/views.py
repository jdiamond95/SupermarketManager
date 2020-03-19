from builtins import len

import os
from django.shortcuts import render, redirect
from django.contrib.auth import login, authenticate
from django.contrib.auth.decorators import login_required
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger

from django.forms.models import modelformset_factory
from django.contrib.auth.models import *

from supermarket_manager.settings import BASE_DIR
from webapp.models import *
from webapp.forms import SignUpForm
from webapp.forms import *
from django.conf import settings
from webapp.models import Item
from webapp.serializers import *
from rest_framework import generics, status, mixins
from django.http.response import HttpResponse
from webapp.permissions import *
from rest_framework.views import APIView
from rest_framework.response import Response
from django.http import Http404
from django.core.mail import EmailMultiAlternatives
import datetime
import vincent
from datetime import timedelta

from django.shortcuts import get_object_or_404

from collections import Counter

def index(request):
    # print(request.)
    user = request.user
    # print(user)
    is_manager = True
    branch = None
    if str(user) == "AnonymousUser":
        is_manager = False
    else:
        try:
            branch = SupermarketBranch.objects.get(manager=request.user)
            print(branch)
        except SupermarketBranch.DoesNotExist:
            is_manager = False
    # print(is_manager)
    # print(request.user)
    context = {
        'is_manager': is_manager,
        'branch': branch
    }
    return render(request, "webapp/index.html", context)

def custom404(request):
    return render(request, "webapp/custom404.html", None)

def signup(request):
    print("Signup")
    if request.method == 'POST':
        form = SignUpForm(request.POST)
        if form.is_valid():
            form.save()
            username = form.cleaned_data.get('username')
            raw_password = form.cleaned_data.get('password1')
            user = authenticate(username=username, password=raw_password)
            login(request, user)
            email = request.POST.get("email")
            firstName = request.POST.get("first_name")


            subject = 'Welcome to SuprMarkt!!!'
            from_email = settings.EMAIL_HOST_USER
            to_email = []
            to_email.append(email)
            message = ""
            realMessage = "<h2>Hey " + str(firstName) + ",</h2><br>Congratulations on joining SuperMarkt!! Find a branch near you and get shopping today!"
            msg = EmailMultiAlternatives(subject, message, from_email, to_email)
            msg.attach_alternative(realMessage, "text/html")
            msg.send()

            return redirect('webapp:home')
    else:
        form = SignUpForm()
    return render(request, 'webapp/registration/signup.html', {'form': form})


def about_us(request):
    return render(request, 'webapp/about_us.html')

@login_required
def view_profile(request, id):
    try :
        curr_user = User.objects.get(id=id)
    except User.DoesNotExist:
        error = "You cannot access other people's profiles!"
        context = {'error': error}
        return render(request, 'webapp/error.html', context)

    if curr_user != request.user:
        error = "You cannot access other people's profiles"
        context = {'error': error}
        return render(request, 'webapp/error.html', context)

    #See if the user is a manager
    try:
        branch = SupermarketBranch.objects.filter(manager=curr_user).first()
    except SupermarketBranch.DoesNotExist:
        branch = None

    #curr_user is NOT a manager
    if branch == None:
        #See if they have added a favourite branch
        try :
            branch = UserMetaData.objects.get(user=curr_user)
        except UserMetaData.DoesNotExist :
            branch = None

        context = {'user': curr_user, 'branch': branch}
        return render(request, 'webapp/user/profile.html', context)

    else:
        context = {'branch': branch, 'user': curr_user,}
        return render(request, 'webapp/stores/manager_profile.html', context)


def company_list(request):
    companies = Company.objects.all()
    context = {'companies': companies}
    return render(request, 'webapp/stores/companies.html', context)


def branches_list(request, company_name):
    branches = SupermarketBranch.objects.filter(company__name=company_name)
    context = {'company_name' : company_name,
               'branches': branches}
    return render(request, 'webapp/stores/branches.html', context)


# def store_description(request, company_name):
def store_description(request, company_name, store_id):
    store_inventory = BranchInventory.objects.filter(
        supermarketBranch__company__name=company_name,
        supermarketBranch__id=store_id)

    query = request.GET.get("q")
    page = request.GET.get('page')
    print("query: {}".format(query))

    if query:
        print("The query: {}".format(query) )
        try:
            aisle_num = int(query)

            store_inventory = store_inventory.filter(aisle__aisleNum=aisle_num)
            print("AISLE Q")

        except:
            print("Product NAME")
            store_inventory = store_inventory.filter(item__name__contains=query)



    # if query:
    #     if ()
    #     store_inventory = store_inventory.filter(item__name__contains=query)
    print(store_inventory)
    store_name = SupermarketBranch.objects.get(id=store_id).branchName


    paginator = Paginator(store_inventory, 5)
    try:
        product_list = paginator.page(page)
    except PageNotAnInteger:
        product_list = paginator.page(1)
    except EmptyPage:
        product_list = paginator.page(paginator.num_pages)

    context = {'company_name': company_name,
               'store_name': store_name,
               # 'store_inventory': store_inventory}
               'store_inventory': product_list}

    return render(request, 'webapp/stores/store.html', context)


@login_required
def favourite_branch(request, user_id):
    print("METHOD: {}".format(request.method))
    print("The id is: {}".format(user_id))
    try :
        curr_user = User.objects.get(id=user_id)
    except User.DoesNotExist:
        error = "You cannot access other people's profiles!"
        context = {'error': error}
        return render(request, 'webapp/error.html', context)

    branches = SupermarketBranch.objects.all()

    if request.method == 'POST':
        print(request.POST)
        fav_branch_str = request.POST.get('branch')
        #TODO handle empty submission on frontend
        if fav_branch_str == None:
            return redirect("webapp:view_profile", request.user.id)

        branch = SupermarketBranch.objects.get(branchName=fav_branch_str)
        print("The favorite branch name is: {}".format(fav_branch_str))

        # Does the user already have a favourite branch?
        # test = None
        try:
            test = UserMetaData.objects.get(user=curr_user)

        except UserMetaData.DoesNotExist:
            test = None

        if test is None:
            # Current user doesnt have a favour/admin/login/?next=/admin/ite. Set as the supplied favorite branch
            new_fav = UserMetaData(user=curr_user, branch=branch)
            new_fav.save()
        else:
            # Overwrite the existing favorite with the newly supplied favorite
            test = UserMetaData.objects.get(user=curr_user)
            test.branch = branch
            test.save()
        # Return to user profile page
        return redirect("webapp:view_profile", request.user.id)

    # Supply the favorite branch selection page with the correct filter and pagination applied
    page = request.GET.get('page')
    query = request.GET.get("q")
    if query:
        branches = branches.filter(
            branchName__contains=query) | branches.filter(
            address__contains=query) | branches.filter(
            company__name__contains=query)
    paginator = Paginator(branches, 5)
    try:
        branch_list = paginator.page(page)
    except PageNotAnInteger:
        branch_list = paginator.page(1)
    except EmptyPage:
        branch_list = paginator.page(paginator.num_pages)

    # Return with the appropriate branches avaliable
    context = {'branches': branch_list, 'user_id': user_id}
    return render(request,
                  "webapp/user/favourite_branch.html", context)


# @login_required
# def submit_favourite_branch(request):
#     print("===>ENTERED")
#     branch_string = request.GET.get('branch')
#     branch = SupermarketBranch.objects.get(branchName=branch_string)
#     curr_user = User.objects.get(id=request.user.id)
#
#
#     #Does the user already have a favourite branch?
#     try:
#         test = UserMetaData.objects.get(user=curr_user)
#
#     #Current user doesnt have a favourite. Make a new one
#     except UserMetaData.DoesNotExist:
#         test = None
#
#
#     if test == None:
#         newFav = UserMetaData(user=curr_user, branch=branch)
#         newFav.save()
#     else:
#         test = UserMetaData.objects.get(user=curr_user)
#         test.branch = branch
#         test.save()
#
#     return redirect("webapp:view_profile", request.user.id)





@login_required
def transaction_history(request, id):
    print('The user id is: {}'.format(id))
    print('The request id is: {}'.format(request.user.id))
    try :
        user = User.objects.get(id=id)
    except User.DoesNotExist:
        error = "You cannot access other people's transaction history!"
        context = {'error': error}
        return render(request, 'webapp/error.html', context)

    if user != request.user:
        error = "You cannot access other people's transaction history!"
        context = {'error': error}
        return render(request, 'webapp/error.html', context)
    else:
        page = request.GET.get('page')
        t_hist = Transaction.objects.all().filter(user=user)
        paginator = Paginator(t_hist, 10)
        try:
            product_list = paginator.page(page)
        except PageNotAnInteger:
            product_list = paginator.page(1)
        except EmptyPage:
            product_list = paginator.page(paginator.num_pages)
        context = {'user': user,
                   # 'transaction_history': t_hist}
                   'transaction_history': product_list}

        return render(request, 'webapp/user/transaction_history.html', context)


@login_required
def transaction_details(request, id, t_id):
    try :
        user = User.objects.get(id=id)
    except User.DoesNotExist:
        error = "You cannot access other people's transaction history!"
        context = {'error': error}
        return render(request, 'webapp/error.html', context)
    transaction = Transaction.objects.all().filter(id=t_id)[0]
    print(type(transaction))
    transaction_data = TransactionData.objects.all().filter(transaction_id=transaction.id)
    t_data = list()
    for transact in transaction_data:
        t_item = BranchInventory.objects.all().filter(item__barcode=transact.item.barcode)[0]
        total_cost = transact.quantity * t_item.price
        t_data.append((transact, t_item, total_cost))

    print(type(transaction_data))
    context = {'user': user,
               'transaction' : transaction,
               'transaction_data': t_data}

    return render(request, 'webapp/user/transaction_details.html', context)



@login_required
def branch_inventory_manage(request, id, company_name, branch_name):
    try :
        curr_user = User.objects.get(id=id)
    except User.DoesNotExist:
        error = "You cannot access other branch's profiles!"
        context = {'error': error}
        return render(request, 'webapp/error.html', context)
    branch = SupermarketBranch.objects.get(manager=curr_user)
    products = BranchInventory.objects.filter(supermarketBranch=branch)

    page = request.GET.get('page')
    query = request.GET.get("q")

    if query:
        products = products.filter(item__name__contains=query)
    paginator = Paginator(products, 5)
    try:
        product_list = paginator.page(page)
    except PageNotAnInteger:
        product_list = paginator.page(1)
    except EmptyPage:
        product_list = paginator.page(paginator.num_pages)

    context = {'manager': curr_user, 'branch': branch, 'products': product_list}

    if request.method == 'POST':
        if request.POST.get("item-name") is not None:
            #Pull form details from POST request
            print("ITEM NAME = " + str(request.POST.get("item-name")))
            item = Item.objects.get(name=request.POST.get("item-name"))



            if 'delete-item' in request.POST:

                BranchInventory.objects.get(supermarketBranch=branch, item=item).delete()

            elif 'update-item' in request.POST:
                # price = request.POST.get("price")
                category = request.POST.get("category")
                try:
                    price = float(request.POST.get("price"))
                except ValueError as err:
                    print("PRICE ERR")
                    context.update({'form_error_price': 'This field only accepts numerics'})
                    return render(request, 'webapp/stores/manage_inventory.html', context)

                try:
                    quantity = float(request.POST.get("quantity"))
                except ValueError as err:
                    print("QUAN ERR")
                    context.update({'form_error_quan': 'This field only accepts numerics'})
                    return render(request, 'webapp/stores/manage_inventory.html', context)

                try:
                    aislePOST = int(request.POST.get("aisle"))
                except ValueError as err:
                    print("AISLE ERR")
                    context.update({'form_error_aisle': 'This field only accepts numerics'})
                    return render(request, 'webapp/stores/manage_inventory.html', context)

                try:
                    restockAlert = int(request.POST.get("restockAlert"))
                except ValueError as err:
                    print("RESTOCK ALERT ERR")
                    context.update({'restock_error': 'This field only accepts numerics'})
                    return render(request, 'webapp/stores/manage_inventory.html', context)

                print("price: {}".format(price))
                print("restockAlert: {}".format(restockAlert))
                print("quantity: {}".format(quantity))
                print("category: {}".format(category))
                print("aisle: {}".format(aislePOST))

                try:
                    aisle = Aisle.objects.get(supermarketBranch=branch,aisleNum=aislePOST)
                except Aisle.DoesNotExist:
                    aisle = Aisle(aisleNum=request.POST.get("aisle"), name="", description="", supermarketBranch=branch)
                    aisle.save()

                branch_item = BranchInventory.objects.get(supermarketBranch=branch, item=item)

                branch_item.price = price
                branch_item.quantity = quantity
                print("initial category: {}".format(branch_item.item.category))
                print("\tupdating wih: {}".format(category))
                print("POST category: {}".format(branch_item.item.category))
                branch_item.aisle = aisle

                branch_item.restockAlert = restockAlert

                branch_item.item.save()
                branch_item.save()
                context.update({'form_updated': branch_item})
        else:
            try:
                if request.FILES['myfile'] and request.POST.get('file_name'):

                    print("File Upload")
                    file_name = request.POST.get('file_name')
                    print(request.FILES['myfile'])
                    print(file_name)
                    f = request.FILES['myfile']
                    file_p = os.path.join(BASE_DIR, "webapp/static/webapp/product_images/",file_name + ".jpg")
                    print(file_p)
                    with open(file_p, 'wb+') as destination:
                        for chunk in f.chunks():
                            print(chunk)
                            destination.write(chunk)
                    print("DONE")
            except:
                context.update(
                    {'upload_error': 'Could not upload image'})
                return render(request,
                              'webapp/stores/manage_inventory.html',
                                  context)

    # elif request.method == 'GET':

    return render(request, 'webapp/stores/manage_inventory.html', context)



@login_required
def branch_analytics_manage(request, id, company_name, branch_name):
    try :
        curr_user = User.objects.get(id=id)
    except User.DoesNotExist:
        error = "You cannot access other branch's profiles!"
        context = {'error': error}
        return render(request, 'webapp/error.html', context)
    branch = SupermarketBranch.objects.get(manager=curr_user)
    currentTime = datetime.datetime.now()

    context = {'manager': curr_user, 'branch': branch, 'company_name' : company_name}
    print("Analytics for:")
    print("\tCompany: {}".format(company_name))
    print("\tBranch: {}".format(branch_name))

    # TODO expand to filter by week and oterh params
    # transactions = Transaction.objects.filter(supermarketBranch=branch_name, supermarketBranch__company__name=company_name)
    transactions = Transaction.objects.filter(supermarketBranch__branchName__contains=branch_name)
    # ,                                              supermarketBranch__company__name__exact=company_name)
    print("WE have found: {} transactions for that query".format(len(transactions)))
    if len(transactions) == 0:
        context.update({'store_analytics' : None})
        return render(request, 'webapp/stores/manager_store_analytics.html', context)
        # return HttpResponse("No DATA")



    transactions = Transaction.objects.filter(supermarketBranch=branch)
    numTransactions = len(transactions)

    #Number of transactions
    transactionsThisMonth = len(transactions.filter(time__month=currentTime.month))



    #Total Value of Sales

    total_sales = 0
    for t in transactions:
        total_sales += t.total_price

    print("total sales "+ str(total_sales))





    t_data = list()
    transac_total = dict()
    for t in transactions:
        transaction_data = TransactionData.objects.all().filter(transaction_id=t.id)
        t_data.append((t, transaction_data))

    # Iterate over each transaction and agreegate the details of each transaction
    for transact in t_data:
        print(transact)
        for transac_d in transact[1]:

            try:
                branch_item = BranchInventory.objects.all().filter(item__barcode=transac_d.item.barcode,
                                                                   supermarketBranch__branchName__exact=branch_name).first()
                print(branch_item)
                print(transac_d.item.name, end="-")
                print(transac_d.quantity)
                # print(">>{}".format(branch_item))
                print(">>{}".format(branch_item.price))
                # print()

                data = (transac_d.quantity, branch_item.price)

                if transac_d.item.name in transac_total.keys():
                    tup = transac_total[transac_d.item.name]
                    print(">>>>>>Update")
                    print(tup)
                    q = tup[0]
                    q = q + transac_d.quantity
                    tup = (q, tup[1])
                    print(tup)
                    transac_total[transac_d.item.name] = tup
                else:
                    transac_total.update({transac_d.item.name: data})
            except:
                pass

    # Sort by the quantity
    sorted_items = [x for x in transac_total.items()]
    sorted_items.sort(key=lambda x: x[1][0], reverse=True)
    print(sorted_items)

    # sorted_items = sorted(((v, k) for k, v in transac_total.items()), reverse=True)
    # print(sorted_items)

    for x in sorted_items:
        print(x[0])
        print(x[1])

    store_analytics = [
        ('Most Popular', sorted_items[0]),
        ('Least Popular', sorted_items[-1]),
        ('Total Sales', total_sales),
        ('Total Transactions', numTransactions),
        ('Monthly Transactions', transactionsThisMonth),
    ]
    max_quan = sorted_items[0][1][0]
    min_quan = sorted_items[-1][1][0]
    print(max_quan)
    print(min_quan)
    # TODO add ability to ahve more than one max and min
    # for x in sorted_items:
    #     # TODO check this
    #     if x[1][0] == max_quan and x[0] != store_analytics[0][1][0]:
    #         print(store_analytics[0][1][0])
    #         print("EXTRRA MAX FOUND")
    #         store_analytics.append(('Most Popular', x))
    #     elif x[1][0] == min_quan and x[0] != store_analytics[1][1][0]:
    #         print(store_analytics[1][1][0])
    #         print("EXTRRA MIN FOUND")
    #
    #         store_analytics.append(('Least Popular', x))

    context.update({'store_analytics' : store_analytics})
    print("...................")
    print(context)




    #GRAPHS
    branch_transactions = Transaction.objects.filter(supermarketBranch=branch)

    if request.method == "POST":
        try:
            numMonths = int(request.POST.get('months'))
        except:
            numMonths = 6
    else:
        numMonths = 6
    print("The number of months: {}".format(numMonths))

    labels = []
    values = []
    all_branch_items = TransactionData.objects.filter(transaction__supermarketBranch=branch)
    branch_items = TransactionData.objects.none()
    for index in range(0, numMonths):
        tempDatetime = currentTime - datetime.timedelta(days=(index*30))
        tempMonthName = tempDatetime.strftime('%B')
        monthTransactions = Transaction.objects.filter(time__month=tempDatetime.month)
        montlyBranchItems = all_branch_items.filter(transaction__time__month=tempDatetime.month)
        branch_items = branch_items | montlyBranchItems
        tempValue = 0
        for t in monthTransactions:
            tempValue += t.total_price

        labels.insert(0, tempMonthName)
        values.insert(0, int(tempValue))


    #Categories for pie chart
    categories = {}
    for b in branch_items:
        key = str(b.item.category)
        print("Item : " + str(key))
        if key in categories:
            categories[key] += 1
            print("Found a repeat\n")
        else:
            categories[key] = 1
            print("Found a new category\n")

    pie = vincent.Pie(categories)
    pie.legend('Category Sales')
    pie.to_json('webapp/static/webapp/categories_pie.json')


    #Total Sales Graph
    data = {'data': values, 'x': labels}
    bar = vincent.Bar(data, iter_idx='x')
    bar.axis_titles(x="Month", y="Total Sales")
    bar.colors(brew='Set1')
    bar.to_json('webapp/static/webapp/sales_graph.json')

    # context.update({"numMonths": numMonths})

    return render(request, 'webapp/stores/manager_store_analytics.html', context)


# ---------------------------------------------
# REST VIEWS
# ---------------------------------------------


class MobileAuthenticate(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request, format=None):
        return HttpResponse("Success")


class ItemList(generics.ListCreateAPIView):
    queryset = Item.objects.all()
    serializer_class = ItemSerializer
    # permission_classes = (IsManager,)
    permission_classes = (permissions.IsAuthenticated,)

class ItemDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = Item.objects.all()
    serializer_class = ItemSerializer
    # permission_classes = (IsManager,)
    permission_classes = (permissions.IsAuthenticated,)


class SupermarketBranchList(APIView):
    # permission_classes = (IsManager,)
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request, format=None):
        queryset = SupermarketBranch.objects.all()
        serializer = SupermarketBranchSerializer(queryset, many=True)
        return Response(serializer.data)


class SupermarketBranchDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = SupermarketBranch.objects.all()
    serializer_class = SupermarketBranchSerializer
    # permission_classes = (IsManager,)
    permission_classes = (permissions.IsAuthenticated,)


class BranchInventoryList(APIView):
    # permission_classes = (IsManager,)
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request, branch_id, format=None):
        queryset = BranchInventory.objects.filter(supermarketBranch=branch_id)
        serializer = BranchInventoryGetSerializer(queryset, many=True)
        return Response(serializer.data)

    def post(self, request, branch_id, format=None):
        serializer = BranchInventorySerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class BranchInventoryDetail(APIView):
    # permission_classes = (IsManager,)
    permission_classes = (permissions.IsAuthenticated,)

    def get_object(self, branch_id, barcode):
        try:
            return BranchInventory.objects.get(supermarketBranch=branch_id, item=barcode)
        except BranchInventory.DoesNotExist:
            raise Http404

    def get(self, request, branch_id, barcode, format=None):
        queryset = self.get_object(branch_id, barcode)
        serializer = BranchInventoryGetSerializer(queryset)
        return Response(serializer.data)

    def put(self, request, branch_id, barcode, format=None):
        queryset = self.get_object(branch_id, barcode)
        serializer = BranchInventorySerializer(queryset, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, branch_id, barcode, format=None):
        queryset = self.get_object(branch_id, barcode)
        queryset.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class UserList(generics.ListAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    # permission_classes = (IsManager,)
    permission_classes = (permissions.IsAuthenticated,)


class UserCreate(generics.CreateAPIView):
    print("Creating user")
    queryset = User.objects.all()
    serializer_class = UserSerializer


class UserDetail(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request, format=None):
        queryset = User.objects.filter(username=request.user)
        serializer = UserAuthenticateSerializer(queryset, many=True)
        return Response(serializer.data)


class TransactionList(generics.ListAPIView):
    serializer_class = TransactionSerializer
    permission_classes = (permissions.IsAuthenticated,)
    queryset = Transaction.objects.all()


class TransactionUserList(generics.ListAPIView):
    serializer_class = TransactionSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_queryset(self):
        user_name = self.kwargs['user_name']
        return Transaction.objects.filter(user__username=user_name)


class TransactionDetail(generics.RetrieveAPIView):
    serializer_class = TransactionSerializer
    permission_classes = (permissions.IsAuthenticated,)
    queryset = Transaction.objects.all()


class ShoppingCartList(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request, user_name, format=None):
        queryset = ShoppingCart.objects.filter(user__username=user_name)
        serializer = ShoppingCartSerializer(queryset, many=True)
        print(serializer.data)
        for tmp in serializer.data:
            print(serializer.data)

        return Response(serializer.data)

    def post(self, request, user_name, format = None):
        userId = User.objects.get(username=user_name).id
        itemID = BranchInventory.objects.filter(supermarketBranch_id=request.data.get('branch')).get(item__barcode=request.data.get('barcode')).id
        sData = {'user':userId, 'branch':request.data.get
        ('branch'),'item':itemID, 'quantity':request.data.get('quantity')}
        serializer = ShoppingCartSerializer(data=sData)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, user_name, format=None):
        cartObjects = ShoppingCart.objects.filter(user__username=user_name)
        if len(cartObjects) != 0:
            for c in cartObjects:
                c.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class ShoppingCartDetail(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def get_object(self, user_name, barcode):
        try:
            return ShoppingCart.objects.filter(user__username=user_name).get(item__item__barcode=barcode)
        except ShoppingCart.DoesNotExist:
            raise Http404

    def get(self, request, user_name, barcode, format=None):
        queryset = self.get_object(user_name, barcode)
        serializer = ShoppingCartSerializer(queryset)
        return Response(serializer.data)

    def put(self, request, user_name, barcode, format=None):
        queryset = self.get_object(user_name, barcode)
        userId = User.objects.get(username=request.data.get('user')).id
        itemID = BranchInventory.objects.filter(supermarketBranch_id=request.data.get('branch')).get(
            item__barcode=request.data.get('barcode')).id
        sData = {'user': userId, 'branch': request.data.get
        ('branch'), 'item': itemID, 'quantity': request.data.get('quantity')}
        serializer = ShoppingCartSerializer(queryset, data=sData)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, user_name, barcode, format=None):
        queryset = self.get_object(user_name, barcode)
        queryset.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class Checkout(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def emailStockAlert(self, branchInventoryID):
        item = BranchInventory.objects.get(id=branchInventoryID.id)
        manager = item.supermarketBranch.manager
        subject = 'Stock Alert!'
        from_email = settings.EMAIL_HOST_USER
        to_email = []
        to_email.append(manager.email)
        message = ""
        realMessage = "<h2>Hey " + str(manager.get_full_name()) + "</h2><br> You are running low on " + str(item.item.name) + ". Remember to restock soon!<br>SuperMarkt"
        msg = EmailMultiAlternatives(subject, message, from_email, to_email)
        msg.attach_alternative(realMessage, "text/html")
        msg.send()


    def emailReciept(self, transactionID):
        print("HELLLO - email emailReciept")
        transaction = Transaction.objects.get(id=transactionID)
        transaction_items = TransactionData.objects.filter(transaction=transaction)
        subject = 'Your Supermrkt Reciept!'
        from_email = settings.EMAIL_HOST_USER
        to_email = []
        to_email.append(transaction.user.email)
        print("Sending email to " + transaction.user.email)
        message = ""
        realMessage = "<!DOCTYPE html> <html lang='en''> <head> <meta charset='UTF-8'> <title>Title</title> </head> <body> <b>" + str(transaction.user.get_full_name())\
                      + "</b>" + "<p>Heres your reciept from your transaction at " \
                      + str(transaction.supermarketBranch.branchName) + " " + str(transaction.supermarketBranch.company) + "!</p> <ul>"

        item_line_len = 25
        for t in transaction_items:
            # TODO how to fill with dahses
            # {s: {c} ^ {n}}
            item_fill = "{:{c}<20} x {:>3} = ${:>1}".format(str(t.item.name), str(t.quantity), str(t.price), c='-')
            # realMessage += "<li>" + str(t.item.name) + "-------------------- x" + str(t.quantity)+ " = $"+ str(t.price) + "</p></li>"
            realMessage += "<li style><pre>" + item_fill + "</pre></li>"
            print(item_fill)


        realMessage += "</ul> <p>Total: $" + str(transaction.total_price) + "</p>"
        realMessage += "<p>Thanks for shopping with Supermrkt!</p>" + "</body> </html>"
        print(realMessage)

        msg = EmailMultiAlternatives(subject, message, from_email, to_email)
        msg.attach_alternative(realMessage, "text/html")
        msg.send()

    def get(self, request, user_name):
        print("HELLLO-GET")
        #get shopping cart objects of the user
        cartObjects = ShoppingCart.objects.filter(user__username=user_name)
        print("A")
        if len(cartObjects) == 0:
            return Response("No object in shopping cart", status=status.HTTP_400_BAD_REQUEST)
        userID = User.objects.get(username=user_name)
        item_list = []
        update_list = []
        print("B")
        totalPrice = 0
        #for every item in the shopping cart
        for c in cartObjects:
            bItem = BranchInventory.objects.get(id=c.item.id)
            #check if store has enough stock to do it
            if bItem.quantity < c.quantity:
                return Response("This branch does not have enough stock to fulfil the requested sale of "+str(c.item), status=status.HTTP_400_BAD_REQUEST)
            item_list.append((bItem.item, c.quantity, bItem.price))
            update_list.append((bItem, c.quantity))
            totalPrice += bItem.price * c.quantity
        #make transaction
        tmpTrans = Transaction(supermarketBranch=c.branch, user=userID, total_price=totalPrice, time=timezone.now())
        tmpTrans.save()
        print("C")
        for i in item_list:
            tmpData = TransactionData(transaction_id=tmpTrans.id, item=i[0], quantity=i[1], price=i[2])

            tmpData.save()
        print("D")
        #delete objects off the cart
        for c in cartObjects:
            c.delete()
        #update branch stock levels
        for c in update_list:
            c[0].quantity = c[0].quantity - c[1]
            c[0].save()

            print("Checking restock amounts!!")
            if c[0].quantity < c[0].restockAlert:
                self.emailStockAlert(c[0])


        print("E")
        self.emailReciept(tmpTrans.id)



        serializer = TransactionSerializer(tmpTrans)
        return Response(serializer.data)


# class UserDetail(generics.RetrieveAPIView,
#                                mixins.DestroyModelMixin,
#                                mixins.UpdateModelMixin):
#     permission_classes = (
#         permissions.IsAuthenticated,
#         IsCurrentUser,
#     )
#     serializer_class = UserSerializer
#
#     def get_object(self):
#         username = self.kwargs["username"]
#         obj = get_object_or_404(User, username=username)
#         return obj
#
#     def get(self, request, *args, **kwargs):
#         return self.retrieve(request, *args, **kwargs)
#
#     def delete(self, request, *args, **kwargs):
#         return self.destroy(request, *args, **kwargs)
#
#     def put(self, request, *args, **kwargs):
#         return self.update(request, *args, **kwargs)



# class UserDetail(APIView):
#     permission_classes = (IsCurrentUser,)
#
#     def get_object(self, username):
#         try:
#             return User.objects.get(username = username)
#         except User.DoesNotExist:
#             raise Http404
#
#     def get(self, request, username, format=None):
#         queryset = self.get_object(username)
#         serializer = UserSerializer(queryset)
#         return Response(serializer.data)
#
#     def put(self, request, username, format=None):
#         queryset = self.get_object(username)
#         serializer = UserSerializer(queryset, data=request.data)
#         if serializer.is_valid():
#             serializer.save()
#             return Response(serializer.data)
#         return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
#
#     def delete(self, request, username, format=None):
#         queryset = self.get_object(username)
#         queryset.delete()
#         return Response(status=status.HTTP_204_NO_CONTENT)

# class BranchDetail(APIView):
#     def get(self, request, branch_id, format=None):
#         queryset = SupermarketBranch.objects.filter(id=branch_id)
#         serializer = SupermarketBranchSerializer(queryset, many=True)
#         return Response(serializer.data)
