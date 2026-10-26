@file:OptIn(ExperimentalWasmInterop::class)

import kotlin.wasm.ExperimentalWasmInterop
import kotlin.wasm.WasmImport

@WasmImport("wasi_snapshot_preview1", "proc_exit")
private external fun wasiProcExit(status: Int)

actual fun exitProcess(status: Int): Nothing {
    wasiProcExit(status)
    error("proc_exit returned")
}
