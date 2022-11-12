package riscv
import chisel3 . _
import chisel3 . util . _
class immediate_instruction extends Module {
    val io = IO(new Bundle{
        val i_instruction = Input(UInt(32.W))
        val out = Output(SInt(32.W))
    })
    val op_code = io.i_instruction(6,0)


    // i type                           jalr                         lw
    when (op_code==="b0010011".U || op_code==="b1100111".U || op_code==="b0000011".U){
        io.out := (Cat(io.i_instruction(31,20))).asSInt
    }
    //s type
    .elsewhen(op_code==="b0100011".U){
        io.out := (Cat(io.i_instruction(31,25),io.i_instruction(11,7))).asSInt
    }
    //b type
    .elsewhen(op_code==="b1100011".U){
        io.out  := Cat(io.i_instruction(31),io.i_instruction(7),io.i_instruction(30,25),io.i_instruction(11,8),"b0".U).asSInt
    }
    //u type
    .elsewhen(op_code==="b0110111".U){
        io.out := (Cat(io.i_instruction(31,12),"b00000000000".U)).asSInt
    }
    //j type
    .elsewhen(op_code==="b1101111".U){
        io.out := Cat(io.i_instruction(31),io.i_instruction(19,12),io.i_instruction(20),io.i_instruction(30,21),0.U).asSInt
    }
    .otherwise{
        io.out := 0.S
    }
}