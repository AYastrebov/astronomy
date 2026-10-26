/**
 * Terminates the process with the given exit status, so shell scripts
 * (such as demotest) can tell whether a demo succeeded.
 */
expect fun exitProcess(status: Int): Nothing
