""" The main scrapy.spider class for web crawling """

import scrapy
from scrapy.loader import ItemLoader
from ..init import init_proxy, init_query
from .. import helpers
from ..items import Ad

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
        title_paths:
        response_count: The number of responses received (for debug purpose only)
        useful_proxy: The set of proxy addresses that can be connected (for debug purpose only)
        title_path: list of possible paths configurations of each produt
        category_path: List of paths of the the main category
        thumbnail_paths：list of paths of the product image
        price_paths: List of paths of the product price/price range (hidden)
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
        self.title_paths = [
            'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div.a-row.a-spacing-none.scx-truncate-medium.sx-line-clamp-2 > a',
            'div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div.a-row.a-spacing-none.scx-truncate-medium.sx-line-clamp-2 > a',
            'div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a',
            'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a',
            'div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini.sx-line-clamp-4 > a',
            'div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini > a'
        ]
        self.category_paths = [
            'leftNavContainer > ul:nth-child(2) > div > li:nth-child(1) > span > a > h4'
        ]
        self.brand_paths = [
            'div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(2) > span:nth-child(2)',
            'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(2) > span:nth-child(2)'
        ]
        self.thumbnail_paths =[
            'div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img',
            'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img'
        ]
        self.price_paths=[
            'div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(4) > div.a-column.a-span7 > div:nth-child(1) > div:nth-child(3) > a > span.a-offscreen',
            'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div:nth-child(1) > div:nth-child(3) > a > span.a-offscreen',
            'div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div:nth-child(1) > div:nth-child(3) > a > span.a-offscreen',
            'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span.a-offscreen'
            ]
        # self.price_upper_paths = [
        #     'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span.a-color-base.sx-zero-spacing > span > sup.sx-price-fractional',
        #     'div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span.a-color-base.sx-zero-spacing > span > sup.sx-price-fractional',
        #     'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span.a-color-base.sx-zero-spacing > span > sup:nth-child(3)',
        #     'div > div.a-fixed-left-grid > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span.a-color-base.sx-zero-spacing > span > sup:nth-child(7)'
        # ]
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

    def start_requests(self):
        """ Define starting requests urls """
        #queries = (query.query for query in self.query_it)
        queries = ['facial cream']
        for query in queries:
            url = self.query_api + query
            request = scrapy.Request(url = url, callback = self.parse, headers = self.headers) 
            proxy = self.proxy
            request.meta['proxy'] = proxy
            request.meta['query'] = query
            self.logger.debug(f'Send request to url: {url}')
            self.logger.debug(f'Proxy used: {proxy}')
            yield request

    def load_fields(self, loader, response, curr_li):
        """ Load all fields given a response and a result id number in the html unodered list
        Args:
            loader: The ItemLoder object to load items
            response: The raw response from the website
            result_id: The current result id number to be crawled
        Return:
            An Ad object with all fields loaded
        """
        self.load_default_fields(loader)
        self.load_title(loader, response, curr_li)
        self.load_price(loader, response, curr_li)
        self.load_thumbnail(loader, response, curr_li)
        self.load_brand(loader, response, curr_li)
        self.load_category(loader, response, curr_li)
        return loader.load_item()
    
    def load_title(self, loader, response, curr_li):
        for title_path in self.title_paths:
            title_a = curr_li.css(title_path)
            if title_a:
                title = title_a.css('::attr(title)').extract()
                url = title_a.css('::attr(href)').extract()
                print (title)
                print (url)
                loader.add_value('title', title[0])
                loader.add_value('detail_url', url[0])
                return
        self.logger.debug('Not found query because of title: ' + response.request.meta['query'])

    def load_price(self, loader, response, curr_li):
        for price_path in self.price_paths:
            price_str = curr_li.css(price_path+'::text').extract()
            if price_str:
                print (price_str[0])
                # the price_str is a list contains a price string seems like '$68.99 - $89.99' or single price '$45.99'
                multi_price = price_str[0].split('-')
                price = float(multi_price[0].split('$')[1]) # ['', '68.99']
                print (price)
                loader.add_value('price', price)
                return
        self.logger.debug('Not found query because of price: ' + response.request.meta['query'])
    
    def load_thumbnail(self, loader, response, curr_li):
        pass

    def load_brand(self, loader, response, curr_li):
        pass

    def load_category(self, loader, response, curr_li):
        pass
    
    def load_default_fields(self, loader):
        loader.add_value('key_words', '')
        loader.add_value('relevance_score', 0.)
        loader.add_value('p_click', 0.)
        loader.add_value('rank_score', 0.)
        loader.add_value('quality_score', 0.)
        loader.add_value('cost_per_click', 0.)
        loader.add_value('position', 0)

    def parse(self, response):
        """ For testing purpose, try to see if the crawler can get responses from server """
        try:
            result_id = 0
            curr_li = response.css(f'#result_{result_id}')
            while curr_li:
                loader = scrapy.loader.ItemLoader(item = Ad(), response = response)
                ad = self.load_fields(loader, response, curr_li)
                result_id += 1
                curr_li = response.css(f'#result_{result_id}')
                yield ad
        except Exception as e:
            self.logger.error(str(e))
        else:
            self.response_count += 1
            self.useful_proxy.add(response.request.meta['proxy'])
        finally:
            self.logger.debug(f'Total number of responses received: {self.response_count}')
            self.logger.debug(f'All usefull proxies: {self.useful_proxy}')
