import random
from faker import Faker


TOPICS_DISTRIBUTION = [
    ("news", 0.6),
    ("music", 0.2),
    ("sports", 0.1),
    ("games", 0.05),
    ("movies", 0.02),
    ("pets", 0.01),
    ("books", 0.01),
    ("memes", 0.007),
    ("travel", 0.002),
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
