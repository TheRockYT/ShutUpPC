package therockyt.shutuppc.error.errors

import therockyt.shutuppc.error.Error

class PingError(ip: String, e: String) : Error("Failed to ping $ip > $e") {
}