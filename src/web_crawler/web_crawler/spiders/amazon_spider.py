""" The main scrapy.spider class for web crawling """

import scrapy
from scrapy.loader import ItemLoader
from ..init import init_proxy, init_query
from .. import helpers
from ..items import Ad
from re import sub
from decimal import Decimal

LOGGER_NAME = 'spider'

class AmazonSpider(scrapy.Spider):
    """ The main spider class for crawling product information and output to json 
    Class Attributes:
        name: name of the spider object, required by scrapy 
    Attributes:
        _proxy_it: The iterator to generate all proxy (protected)
        _logger: Dummy attribute for overriding the logger defined in scrapy.spider
        query_it: The iterator to generate all query
        query_api: The api for searching on Amazon
        headers: To be filled in the header of http requests
        response_count: The number of responses received (for debug purpose only)
        useful_proxy: The set of proxy addresses that can be connected (for debug purpose only)
    """
    # set name of the spider
    name = 'amazon'

    def __init__(self):
        self._proxy_it = init_proxy.init_proxy()
        self._logger = helpers.setup_config_logger(LOGGER_NAME)[1]
        self.query_it = init_query.init_query()
        self.query_api = 'https://www.amazon.com/s/ref=nb_sb_noss?field-keywords='
        self.headers = {
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Encoding': 'gzip, deflate, sdch, br',
            'Accept-Language': 'en-US,en;q=0.8'
        }
        self.response_count = 0
        self.useful_proxy = set()
        super().__init__()

    @property
    def proxy(self):
        """ The proxy to use for the current request """
        try:
            proxy = next(self._proxy_it)
        except StopIteration:
            self.logger.debug('Finsh one proxy rotation')
            self._proxy_it = init_proxy.init_proxy()
            proxy = next(self._proxy_it)
        return proxy.address

    @property
    def logger(self):
        """ Override the scrapy logger attribute """
        return self._logger

    def add_query_meta(self, query, meta):
        """ Add query information into request object so that it can be accessed via response
        Args:
            query: The Query object of which the information should be recorded
            meta: The meta field of a request object
        Return:
            Void
        """
        meta['query'] = query.query
        meta['campaign_id'] = query.campaign_id
        meta['query_group_id'] = query.query_group_id
        meta['bid_price'] = query.bid_price

    def start_requests(self):
        """ Define starting requests urls """
        # queries = (query.query for query in self.query_it)
        queries = ['facial cream']
        # for query in queries:
        #     url = self.query_api + query
        # queries = (query.query for query in self.query_it)
        for query in self.query_it:
            url = self.query_api + query.query
            request = scrapy.Request(url = url, callback = self.parse, headers = self.headers) 
            proxy = self.proxy
            request.meta['proxy'] = proxy
            self.add_query_meta(query, request.meta)
            self.logger.debug(f'Send request to url: {url}')
            self.logger.debug(f'Proxy used: {proxy}')
            yield request

    def parse(self, response):
        """ For testing purpose, try to see if the crawler can get responses from server """
        # The ad id to be assigned to each crawled id (each time increment by 1)
        ad_id = 0
        try:
            # The result id number in the li element of the ul element of amazon website
            result_id = 0
            curr_li = response.css(f'#result_{result_id}')
            # stop when no more li element exists in the ul element
            while curr_li:
                loader = ItemLoader(item = Ad(), response = response)
                # load all fields into Ad object and return it
                ad = AdsLoader.load_fields(loader, response, curr_li, ad_id)
                # increment result id number and ad id number
                result_id += 1
                curr_li = response.css(f'#result_{result_id}')
                ad_id += 1
                yield ad
        except Exception as e:
            self.logger.error(str(e))
        else:
            # count the number of responses received
            self.response_count += 1
            # add a proxy since it succeeds to respond
            self.useful_proxy.add(response.request.meta['proxy'])
        finally:
            self.logger.debug(f'Total number of responses received: {self.response_count}')
            self.logger.debug(f'All usefull proxies: {self.useful_proxy}')

class AdsLoader:
    """ A helper static class for storing paths to amazon products and loading functions
    Attributes:
        title_path: list of possible paths configurations of each produt
        category_path: List of paths of the the main category
        thumbnail_paths：list of paths of the product image
        price_paths: List of paths of the product price/price range (hidden)
    """
    
    title_paths = [
        'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div.a-row.a-spacing-none.scx-truncate-medium.sx-line-clamp-2 > a',
        'div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div.a-row.a-spacing-none.scx-truncate-medium.sx-line-clamp-2 > a',
        'div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a',
        'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a',
        'div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini.sx-line-clamp-4 > a',
        'div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini > a'
    ]
    category_paths = [
        '#leftNavContainer > ul:nth-child(2) > div > li:nth-child(1) > span > a > h4'
    ]
    brand_paths = [
        'div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(2) > span:nth-child(2)',
        'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(2) > span:nth-child(2)'
    ]
    thumbnail_paths =[
        'div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img',
        'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img'
    ]
    price_paths=[
        'div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(4) > div.a-column.a-span7 > div:nth-child(1) > div:nth-child(3) > a > span.a-offscreen',
        'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div:nth-child(1) > div:nth-child(3) > a > span.a-offscreen',
        'div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div:nth-child(1) > div:nth-child(3) > a > span.a-offscreen',
        'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span.a-offscreen'
    ]
    
    @classmethod
    def get_logger(cls):
        """ Return the logger object corresponding to web crawling 
        (WARNING: has to be used after initialization of an instance of AmazonSpider)
        """
        return helpers.get_logger(LOGGER_NAME)

    @classmethod
    def load_fields(cls, loader, response, curr_li, ad_id):
        """ Load all fields given a response and a result id number in the html unodered list
        Args:
            loader: The ItemLoder object to load items
            response: The raw response from the website
            result_id: The current result id number to be crawled
            ad_id: The id of ad
        Return:
            An Ad object with all fields loaded
        """
        cls.load_query_fields(loader, response, ad_id)
        cls.load_title(loader, response, curr_li)
        cls.load_price(loader, response, curr_li)
        cls.load_thumbnail(loader, response, curr_li)
        cls.load_brand(loader, response, curr_li)
        cls.load_category(loader, response)
        return loader.load_item()
    
    @classmethod
    def load_title(cls, loader, response, curr_li):
        """ Load crawled ads title into Ad object """
        for title_path in cls.title_paths:
            title_a = curr_li.css(title_path)
            if title_a:
                title = title_a.css('::attr(title)').extract()
                url = title_a.css('::attr(href)').extract()
                loader.add_value('title', title[0])
                loader.add_value('detail_url', url[0])
                return
        cls.get_logger().debug('Not found query because of title: ' + response.request.meta['query'])

    @classmethod
    def load_price(cls, loader, response, curr_li):
        """ Load crawled ads price into Ad object """
        for price_path in cls.price_paths:
            price_str = curr_li.css(price_path+'::text').extract()
            if price_str:
                # the price_str is a list contains a price string seems like '$68.99 - $89.99' or single price '$45.99'
                multi_price = price_str[0].split('-')
                price = Decimal(sub(r'[^\d.]', '', multi_price[0]))
                print (price)
                loader.add_value('price', price)
                return
        cls.get_logger().debug('Not found query because of price: ' + response.request.meta['query'])

    @classmethod
    def load_thumbnail(cls, loader, response, curr_li):
        """ Load crawled thumbnail into Ad object """
        for thumbnail_path in cls.thumbnail_paths:
            thumbnail = curr_li.css(thumbnail_path + '::attr(src)').extract()
            if thumbnail:
                loader.add_value('thumbnail', thumbnail[0])
                return
        cls.get_logger().error('Not found query because of thumbnail: ' + response.request.meta['query'])

    @classmethod
    def load_brand(cls, loader, response, curr_li):
        """ Load crawled brand into Ad object """
        for brand_path in cls.brand_paths:
            brand = curr_li.css(brand_path + '::text').extract()
            if brand:
                loader.add_value('brand', brand[0])
                return
        cls.get_logger().error('Not found query because of brand: ' + response.request.meta['query'])

    @classmethod
    def load_category(cls, loader, response):
        """ Load crawled category into Ad object """
        for category_path in cls.category_paths:
            category = response.css(category_path + '::text').extract()
            if category:
                loader.add_value('category', category[0])
        cls.get_logger().error('Not found query because of category: ' + response.request.meta['query'])

    @classmethod
    def load_query_fields(cls, loader, response, ad_id):
        """ Load Ad fields contained in request """
        request_meta = response.request.meta
        loader.add_value('ad_id', ad_id)
        loader.add_value('campaign_id', request_meta['campaign_id'])
        loader.add_value('bid_price', request_meta['bid_price'])
        loader.add_value('query_group_id', request_meta['query_group_id'])
        loader.add_value('query', request_meta['query'])
    