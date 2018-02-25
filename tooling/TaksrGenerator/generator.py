import json
import requests
import faker
import base64
import random

f = faker.Faker()
f.seed(3423)

base_url = 'http://cmput301.softwareprocess.es:8080/cmput301w18t02/'


class User:
    def __init__(self):
        self.name = f.name()
        self.phoneNumber = f.phone_number()
        self.email = f.email()
        self.username = self.name.split()[0].lower()
        self.profilePicture = str(base64.encodebytes(bytes(f.text(), "utf-8")))

    def __str__(self):
        return f"Name: {self.name}; Username: {self.username}"

    def to_json(self):
        return json.dumps(self.__dict__)


class Task:
    def __init__(self, owner_username, bidders):
        self.owner = owner_username
        self.title = f.bs()
        self.status = random.randint(0, 4)
        self.bids = [Bid(bidder) for bidder in bidders]
        self.description = f.text()
        self.photos = [str(base64.encodebytes(bytes(f.text(), "utf-8"))) for i in range(random.randint(0, 10))]
        self.location = LatLng()
        self.chosenBid = self.choose_bid(bidders)

    @staticmethod
    def choose_bid(bidders):
        if random.choice([True, False]):
            return random.choice(bidders)
        else:
            return None

    def to_json(self):
        return json.dumps(self.__dict__, default=lambda x: x.__dict__)


class Bid:
    def __init__(self, bidder):
        self.bidder = bidder
        self.value = random.randint(0, 99999999)


class LatLng:
    def __init__(self):
        self.LatLng = (random.randint(-90, 90) + random.random(), random.randint(-180, 180) + random.random())


def generate_users(number):
    r = requests.delete(base_url + "user")  # clear all users
    print(r)
    user_list = []
    for i in range(number):
        x = User()
        r = requests.post(base_url + "user", data=x.to_json())
        print(r.content)
        user_list.append(x)
    return user_list


def generate_tasks(num_tasks, people):
    task_list = []
    r = requests.delete(base_url + "task")  # clear all users

    for i in range(num_tasks):
        random.shuffle(people)
        task = Task(people[0], people[1:])
        r = requests.post(base_url + "task", data=task.to_json())
        print(r.content)
        task_list.append(task)
    return task_list


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument("num_users", nargs="?", default=10, type=int, help="Enter the number of users, default is 10")
    parser.add_argument("num_tasks", nargs="?", default=20, type=int,
                        help="Enter the number of tasks, has random number of bids from 0-5; default 20 tasks")

    args = parser.parse_args()
    users = generate_users(args.num_users)
    tasks = generate_tasks(args.num_tasks, [user.username for user in users])
    # print(args.num_users)
    # print(args.num_tasks)
