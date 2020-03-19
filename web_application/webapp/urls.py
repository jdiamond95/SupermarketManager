from django.conf.urls import url, include
from . import views
from webapp import views as core_views

app_name = 'webapp'

urlpatterns = [
    url(r'^index/$', views.index, name='index'),

    # This is a catch all and will refer to the home page
    url(r'^$', views.index, name='home'),
    url(r'^signup/$', core_views.signup, name='signup'),
    # User/ manager profile views
    url(r'^profile/(?P<id>\d+)/$', views.view_profile, name='view_profile'),
    url(r'^profile/(?P<user_id>\d+)/favourite_branch/$', views.favourite_branch, name='favourite_branch'),
    url(r'^profile/(?P<id>\d+)/transaction_history/$', views.transaction_history, name='transaction_history'),
    url(r'^profile/(?P<id>\d+)/transaction_details/(?P<t_id>\d+)/$', views.transaction_details, name='transaction_details'),

    url(r'^profile/(?P<id>\d+)/(?P<company_name>\w+)/(?P<branch_name>\w+)/inventory/$', views.branch_inventory_manage, name='branch_inventory_manage'),
    url(r'^profile/(?P<id>\d+)/(?P<company_name>\w+)/(?P<branch_name>\w+)/analytics/$', views.branch_analytics_manage, name='branch_analytics_manage'),
    url(r'^about_us/$', views.about_us, name='about_us'),

    # Views a
    url(r'^companies/$', views.company_list, name='company_list'),
    url(r'^companies/(?P<company_name>\w+)/$', views.branches_list, name='branch_list'),
    url(r'^companies/(?P<company_name>\w+)/(?P<store_id>\d+)/$', views.store_description, name='store_description'),

    # Manager views
    # url(r'^manage_branch/(?P<store_id>\d+)', views.manage_branch, name='manage_branch'), TODO put this back in


    # url(r'^favourite_branch/$', views.favourite_branch, name='favourite_branch'),
    # url(r'^companies/(?P<company_name>\w+)/(?P<branch_name>\w+)/$', views.store_description, name='store_description'),
    url(r'^items/$', views.ItemList.as_view()),
    url(r'^items/(?P<pk>\w+)/$', views.ItemDetail.as_view()),
    url(r'^branches/$', views.SupermarketBranchList.as_view()),
    url(r'^branches/(?P<pk>\d+)/$', views.SupermarketBranchDetail.as_view()),
    url(r'^branches/(?P<branch_id>\d+)/inventory/$', views.BranchInventoryList.as_view()),
    url(r'^branches/(?P<branch_id>\d+)/inventory/(?P<barcode>\w+)/$', views.BranchInventoryDetail.as_view()),
    url(r'^users/$', views.UserList.as_view()),
    url(r'^users/register/$', views.UserCreate.as_view()),
    url(r'^transactions/$', views.TransactionList.as_view()),
    url(r'^transactions/user/(?P<user_name>\w+)/$', views.TransactionUserList.as_view()),
    url(r'^transactions/transaction/(?P<pk>\d+)/$', views.TransactionDetail.as_view()),
    url(r'^mobile_authenticate/$', views.UserDetail.as_view()),
    url(r'^shoppingcart/(?P<user_name>\w+)/$', views.ShoppingCartList.as_view()),
    url(r'^shoppingcart/(?P<user_name>\w+)/(?P<barcode>\w+)/$', views.ShoppingCartDetail.as_view()),
    url(r'^checkout/(?P<user_name>\w+)/$', views.Checkout.as_view()),
    # url(r'^users/(?P<username>\w+)/$', views.UserDetail.as_view())
]


