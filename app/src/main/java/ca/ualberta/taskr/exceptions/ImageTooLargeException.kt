package ca.ualberta.taskr.exceptions

/**
 * ImageTooLargeException. This is thrown when an image is too large
 * @exception ImageTooLargeException
 */
class ImageTooLargeException(size: Int) : Exception("Image size: $size is too large")