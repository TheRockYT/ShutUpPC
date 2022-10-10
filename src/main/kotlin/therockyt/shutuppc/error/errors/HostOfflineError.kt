package therockyt.shutuppc.error.errors

import therockyt.shutuppc.error.Error

class HostOfflineError(ip: String) : Error("$ip is offline.") {
}