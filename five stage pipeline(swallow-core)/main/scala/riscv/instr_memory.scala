package riscv
import chisel3 . _
import chisel3 . util . _
import chisel3.util.experimental.loadMemoryFromFile
class instr_memory extends Module {
    val io = IO(new Bundle{
        val address = Input(UInt(10.W))
        val r_data = Output(UInt(32.W))
    })
    val initfile = "/home/mohammad/file"
    val memory = Mem(1024,UInt(32.W)) //32 bit memory

    loadMemoryFromFile(memory , initfile)
    val a = memory.read(io.address)
    io.r_data := a
}