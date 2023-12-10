import random
from faker import Faker


TOPICS_DISTRIBUTION = [
    ("topic1", 0.6),
    ("topic2", 0.2),
    ("topic3", 0.1),
    ("topic4", 0.05),
    ("topic5", 0.02),
    ("topic6", 0.01),
    ("topic7", 0.01),
    ("topic8", 0.007),
    ("topic9", 0.002),
    ("", 0.001), # no topic
]
_TOPIC_SAMPLER = []
for topic, prob in TOPICS_DISTRIBUTION:
    _TOPIC_SAMPLER.extend([topic] * int(prob * 1000))

_fake = Faker()


def random_text():
    sentence = _fake.sentence(nb_words=6)
    topic = random.choice(_TOPIC_SAMPLER)
    whisper = (
        f"{sentence} #{topic}"
        if topic
        else sentence
    )
    return whisper
