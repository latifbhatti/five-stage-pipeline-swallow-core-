package riscv
import chisel3 . _
import chisel3 . util . _
class pc_counter_plus_4 extends Module {
    val io = IO(new Bundle{
        val pc = Input(UInt(32.W))
        val out = Output(UInt(32.W))
        val out4 = Output(UInt(32.W))
        val pc_write = Input(Bool())
    })
    val reg = Reg(UInt(32.W))

    when(io.pc_write === 0.B){
        io.out4 := reg + 4.U
    }
    .otherwise{
        io.out4 := reg
    }
    reg := io.pc
    io.out := (reg.asSInt + 0.S).asUInt
}