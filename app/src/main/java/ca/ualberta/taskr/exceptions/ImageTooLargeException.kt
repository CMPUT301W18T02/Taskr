package ca.ualberta.taskr.exceptions


class ImageTooLargeException(size: Int) : Exception("Image size: $size is too large")