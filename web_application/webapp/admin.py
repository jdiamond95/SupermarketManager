from django.contrib import admin
from webapp.models import *


admin.site.register(Company)
admin.site.register(Item)
admin.site.register(SupermarketBranch)
admin.site.register(Aisle)
admin.site.register(BranchInventory)
admin.site.register(Transaction)
admin.site.register(TransactionData)
admin.site.register(ShoppingCart)
admin.site.register(UserMetaData)
