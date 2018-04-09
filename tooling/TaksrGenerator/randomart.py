# Adapted from https://jeremykun.com/2012/01/01/random-psychedelic-art/
import base64
import random
import math
import io
from PIL import Image

random.seed()


class X:
    def eval(self, x, y):
        return x

    def __str__(self):
        return "x"


class Y:
    def eval(self, x, y):
        return y

    def __str__(self):
        return "y"


class SinPi:
    def __init__(self, prob):
        self.arg = buildExpr(prob * prob)

    def __str__(self):
        return "sin(pi*" + str(self.arg) + ")"

    def eval(self, x, y):
        return math.sin(math.pi * self.arg.eval(x, y))


class CosPi:
    def __init__(self, prob):
        self.arg = buildExpr(prob * prob)

    def __str__(self):
        return "cos(pi*" + str(self.arg) + ")"

    def eval(self, x, y):
        return math.cos(math.pi * self.arg.eval(x, y))


class Times:
    def __init__(self, prob):
        self.lhs = buildExpr(prob * prob)
        self.rhs = buildExpr(prob * prob)

    def __str__(self):
        return str(self.lhs) + "*" + str(self.rhs)

    def eval(self, x, y):
        return self.lhs.eval(x, y) * self.rhs.eval(x, y)


def buildExpr(prob=0.99):
    if random.random() < prob:
        return random.choice([SinPi, CosPi, Times])(prob)
    else:
        return random.choice([X, Y])()


def plotIntensity(exp, is_rectangle, pixelsPerUnit=150):
    canvasLength = 2 * pixelsPerUnit + 1
    if is_rectangle:
        canvasWidth = canvasLength * 3
    else:
        canvasWidth = canvasLength
    canvas = Image.new("L", (canvasWidth, canvasLength))

    for py in range(canvasLength):
        for px in range(canvasWidth):
            # Convert pixel location to [-1,1] coordinates
            x = float(px - pixelsPerUnit) / pixelsPerUnit
            y = -float(py - pixelsPerUnit) / pixelsPerUnit
            z = exp.eval(x, y)

            # Scale [-1,1] result to [0,255].
            intensity = int(z * 127.5 + 127.5)
            canvas.putpixel((px, py), intensity)

    return canvas


def plotColor(redExp, greenExp, blueExp, is_rectangle, pixelsPerUnit=150):
    redPlane = plotIntensity(redExp, is_rectangle, pixelsPerUnit)
    greenPlane = plotIntensity(greenExp, is_rectangle, pixelsPerUnit)
    bluePlane = plotIntensity(blueExp, is_rectangle, pixelsPerUnit)
    return Image.merge("RGB", (redPlane, greenPlane, bluePlane))


def generate_image(is_rectangle):
    # img = None
    redExp = buildExpr()
    greenExp = buildExpr()
    blueExp = buildExpr()
    image = plotColor(redExp, greenExp, blueExp, is_rectangle)

    return image


def makeImage(is_rectangle):
    image = generate_image(is_rectangle)
    with io.BytesIO() as f:
        image.save(f, "JPEG")

        im = base64.b64encode(f.getvalue())
        return str(im, 'utf-8')


if __name__ == "__main__":
    print(len(makeImage(False)))
