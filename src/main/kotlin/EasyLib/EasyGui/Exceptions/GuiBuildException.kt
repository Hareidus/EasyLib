package EasyLib.EasyGui.Exceptions

class GuiBuildException : Exception {

    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String,filePath : String,cause: Throwable) : super(cause)
    constructor(cause: Throwable) : super(cause)

}