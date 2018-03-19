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
        # self.profilePicture = str(base64.encodebytes(bytes(f.text(), "utf-8")))
        self.profilePicture = None

    def __str__(self):
        return f"Name: {self.name}; Username: {self.username}"

    def to_json(self):
        return json.dumps(self.__dict__)


class Task:
    def __init__(self, owner_username, bidders):
        self.owner = owner_username
        self.title = f.bs()
        self.status = random.choice(["REQUESTED", "BID", "ASSIGNED", "DONE"])
        self.bids = [Bid(bidder) for bidder in bidders if random.choice([True,False])]
        self.description = f.text().replace("\n", "")
        # self.photos = [str(base64.encodebytes(bytes(f.text(), "utf-8"))) for i in range(random.randint(0, 10))]
        self.photos = []
        self.location = {
            "latitude": random.uniform(53.237022, 52.713658) + random.random(),
            "longitude": random.uniform(-114.094366, -112.748048) + random.random(),
            "altitude": 0.0}
        self.chosenBidder = self.choose_bid(bidders)

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
        self.owner = bidder
        self.amount = round(random.random() * random.randint(0, 10000), 2)


class LatLng:
    def __init__(self):
        self.LatLng = (random.randint(-90, 90) + random.random(), random.randint(-180, 180) + random.random(), 0.0)


def generate_users(number):
    r = requests.delete(base_url + "user/_query", data='{"query": {"match_all": {}}}')  # clear all users
    print(r)
    user_list = []
    for i in range(number):
        x = User()
        r = requests.post(base_url + "user", data=x.to_json())
        print(x)
        user_list.append(x)
    return user_list


def generate_tasks(num_tasks, people):
    task_list = []
    r = requests.delete(base_url + "task")  # clear all users

    for i in range(num_tasks):
        random.shuffle(people)
        task = Task(people[0], people[1:])
        r = requests.post(base_url + "task", data=task.to_json())
        print(task)
        task_list.append(task)
    return task_list


def generate_data(num_tasks, num_users):
    delete_data(num_tasks, num_users)
    users = generate_users(num_users)

    return users, generate_tasks(num_tasks, [user.username for user in users])


def generate_indices(num_tasks, num_users):
    r = requests.post(base_url + "user")
    r = requests.post(base_url + "task")


def delete_indices(num_tasks, num_users):
    r = requests.delete(base_url + "user")
    r = requests.delete(base_url + "task")


def delete_data(num_tasks, num_users):
    r = requests.delete(base_url + "user/_query", data='{"query": {"match_all": {}}}')

    r = requests.delete(base_url + "task/_query", data='{"query": {"match_all": {}}}')


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()

    options = {"generate_data": generate_data,
               "generate_indices": generate_indices,
               "delete_indices": delete_indices,
               "delete_data": delete_data}

    parser.add_argument("mode",
                        help="Generator Mode, options are: delete_data, delete_indices, generate_data, generate_indices")
    parser.add_argument("num_users", nargs="?", default=10, type=int,
                        help="Use with generate_data, Enter the number of users, default is 10")
    parser.add_argument("num_tasks", nargs="?", default=20, type=int,
                        help="Use with generate_data, Enter the number of tasks, has random number of bids from 0-5; default 20 tasks")
    parser.add_argument("base_url", nargs="?", default="http://cmput301.softwareprocess.es:8080/cmput301w18t02/",
                        help="Specify the baseUrl for elasticSearch default is http://cmput301.softwareprocess.es:8080/cmput301w18t02/")
    args = parser.parse_args()

    base_url = args.base_url
    func = options[args.mode]

    result = func(args.num_tasks, args.num_users)

    # print(args.num_users)
    # print(args.num_tasks)
