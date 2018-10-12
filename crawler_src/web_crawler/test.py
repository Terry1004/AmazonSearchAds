from web_crawler.init.init_proxy import init_proxy
from web_crawler.init.init_query import init_query

# for proxy in init_proxy():
#     print(proxy.proxy_address)
count =0
for query in init_query():
    count+=1
    print (query)

print (count)