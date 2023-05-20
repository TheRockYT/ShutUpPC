package therockyt.shutuppc.error.errors

import therockyt.shutuppc.error.Error
import java.io.File

class FileLoadError(file: File, e: String) : Error("Failed to load ${file.absolutePath} > $e") {
}