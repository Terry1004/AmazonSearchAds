import argparse
import json
from pyspark import SparkContext
from pyspark.mllib.feature import Word2Vec


LEARNING_RATE = 0.02
MIN_COUNT = 10
VECTOR_SIZE = 20

def init_parser():
    """ Initialize a cli parser for parsing the paths of the input corpus and output synonyms files (relative 
    to $SPARK_HOME) as well as the number of synonyms to find for each word. The default input path is 
    '../data/corpus.txt'; the default output path is '../data/synonyms.json'; the default number of 
    synonyms is 5.

    Returns:
        A command line parser for parsing the input corpurs and output synonyms files paths.
    """
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '-i', '--input', action = 'store', default = '../AmazonSearchAds/data/corpus.txt',
        help = ('The path of the input corpus data (relative to $SPARK_HOME). ' 
        'Default is \'../AmazonSearchAds/data/corpus.txt\'.'
        )
    )
    parser.add_argument(
        '-o', '--output', action = 'store', default = '../AmazonSearchAds/data/synonyms.json',
        help = ('The path of the output synonyms json data (relative to $SPARK_HOME). '
        'Default is \'../AmazonSearchAds/data/synonyms.json\'.'
        )
    )
    parser.add_argument(
        '-n', '--num_synonyms', action = 'store', default = 5, type = int,
        help = 'The number of synonyms to find for each word'
    )
    return parser

def train(corpus_path, sc):
    """ Use Spark to train the word2vec model on the input corpus data.

    Args:
        corpus_path: The path to the input corpus data (relative to $SPARK_HOME).
        sc: The SparkContext instance for running this application.
    
    Returns:
        The trained word2vec model.
    """
    vocab = sc.textFile(corpus_path).map(lambda line: line.split())
    vocab.cache()
    word2vec = Word2Vec()
    model = word2vec.setLearningRate(LEARNING_RATE).setMinCount(MIN_COUNT).setVectorSize(VECTOR_SIZE).fit(vocab)
    return model

def get_synonyms(model, num_synonyms):
    """ A generator that generates json strings of two entries: 'word' and 'synonyms', where the 
    'synonyms' entry is the list of synonyms of the word in the 'word' entry.

    Args:
        model: The trained word2vec model.
        num_synonyms: The number of synonyms to find for each word.
    
    Yields:
        A json string of two entries: 'word' and 'synonyms', where the 'synonyms' entry is the list 
        of synonyms of the word in the 'word' entry.
    """
    for word in model.getVectors():
        synonyms = model.findSynonyms(word, num_synonyms)
        synonym_entry = {}
        synonym_entry['word'] = word
        synonym_entry['synonyms'] = list(map(lambda t: t[0], synonyms))
        yield json.dumps(synonym_entry)


if __name__ == '__main__':
    parser = init_parser()
    args = parser.parse_args()
    sc = SparkContext('local', 'word2vec')
    model = train(args.input, sc)
    with open(args.output, 'w') as f:
        for line in get_synonyms(model, args.num_synonyms):
            f.write(line + '\n')