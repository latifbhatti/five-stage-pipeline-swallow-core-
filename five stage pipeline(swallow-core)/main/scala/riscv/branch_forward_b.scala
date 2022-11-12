package riscv
import chisel3 . _
import chisel3 . util . _
class branch_forward_b extends Module {
    val io = IO(new Bundle{
        val rs2_in = Input(UInt(5.W))

        val rd_ex_mem = Input(UInt(32.W))
        val rd_id_ex = Input(UInt(32.W))
        val rd_mem_wb = Input(UInt(32.W))
        val reg_write_id_ex = Input(Bool())
        val reg_write_ex_mem = Input(Bool())
        val reg_write_mem_wb = Input(Bool())
        val out = Output(UInt(3.W))
    })
    //forward b
     when((io.reg_write_mem_wb === 1.B) && (io.rd_mem_wb =/= 0.U) && ~(io.reg_write_ex_mem ===1.B && (io.rd_ex_mem =/= 0.U) && (io.rd_ex_mem === io.rs2_in)) && (io.rd_mem_wb === io.rs2_in)){
            io.out := 1.U
    }
    .elsewhen((io.reg_write_ex_mem === 1.B) && (io.rd_ex_mem =/= 0.U) && (io.rd_ex_mem === io.rs2_in)){
            io.out := 2.U
    }
    .elsewhen(io.reg_write_id_ex === 1.B && io.rd_id_ex === io.rs2_in){
        io.out := 3.U
    }
    .otherwise{
        io.out := 0.U
    }
}