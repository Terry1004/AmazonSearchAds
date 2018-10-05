from web_crawler.init.init_proxy import init_proxy
from web_crawler.init.init_query import init_query

for proxy in init_proxy():
    print(proxy.get_address())

# for query in init_query():
#     print (query)