package therockyt.shutuppc.utils

import therockyt.shutuppc.PC
import therockyt.shutuppc.PCState
import therockyt.shutuppc.error.errors.FileLoadError
import therockyt.shutuppc.error.errors.FileSaveError
import java.io.*

class FileUtils {
    companion object {
        @Throws(FileSaveError::class)
        fun setFileContent(file: File, content: ArrayList<String>) {
            try {
                if(!file.parentFile.exists()){
                    file.parentFile.mkdirs()
                }
                if(!file.exists()){
                    file.createNewFile()
                }
                val bw = BufferedWriter(FileWriter(file, false))
                for(str in content){
                    bw.write(str)
                    bw.newLine()
                }
                bw.close()
            } catch (ex: IOException) {
                throw FileSaveError(file, ex.toString())
            }
        }
        @Throws(FileLoadError::class)
        fun getFileContent(file: File): ArrayList<String> {
            val content = ArrayList<String>()
            try {
                if(!file.parentFile.exists()){
                    file.parentFile.mkdirs()
                }
                if(!file.exists()){
                    file.createNewFile()
                }
                val br = BufferedReader(FileReader(file))
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    line?.let { content.add(it) }
                }
                br.close()
            } catch (ex: IOException) {
                throw FileLoadError(file, ex.toString())
            }
            return content
        }
    }
}