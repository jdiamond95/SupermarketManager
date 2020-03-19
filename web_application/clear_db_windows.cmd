rm -f db.sqlite3
cd webapp/migrations
for %%i in (*) do if not %%i == __init__.py rm -f %%i
cd __pycache__
rm -f *
cd ../../..