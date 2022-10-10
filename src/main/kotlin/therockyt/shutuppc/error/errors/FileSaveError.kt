package therockyt.shutuppc.error.errors

import therockyt.shutuppc.error.Error
import java.io.File

class FileSaveError(file: File, e: String) : Error("Failed to save ${file.absolutePath} > $e") {
}