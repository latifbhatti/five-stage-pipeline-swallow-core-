package riscv
import chisel3 . _
import chisel3 . util . _
class if_to_id extends Module {
    val io =  IO(new Bundle{
        val instr_in = Input(UInt(32.W))
        val instr_out = Output(UInt(32.W))

        val pc_in = Input(UInt(32.W))
        val pc_out = Output(UInt(32.W))
        val if_id_write = Input(Bool())

        val instr_h = Input(UInt(32.W))
        val if_id_branch = Input(Bool())

    })
    val a = Reg(UInt(32.W)) //for instr
    val b = Reg(UInt(32.W)) //for address
    
    when(io.if_id_branch === 1.B){
        a := 0.U
    }
    .elsewhen(io.if_id_write === 0.B){
        a := io.instr_in
    }
    .otherwise{
        a := io.instr_h
    }
    b := io.pc_in
    io.instr_out := a
    io.pc_out := b

}