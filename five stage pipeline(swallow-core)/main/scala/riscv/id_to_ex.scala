package riscv
import chisel3 . _
import chisel3 . util . _
class id_to_ex extends Module {
    val io =  IO(new Bundle{
        val rs1_value_in = Input(SInt(32.W))
        val rs1_value_out = Output(SInt(32.W))

        val rs1_in = Input(UInt(5.W))
        val rs1_out = Output(UInt(5.W))

        val rs2_in = Input(UInt(5.W))
        val rs2_out = Output(UInt(5.W))       

        val rs2_value_in = Input(SInt(32.W))
        val rs2_value_out = Output(SInt(32.W))

        val im_in = Input(SInt(32.W))
        val im_out = Output(SInt(32.W))

        val instr_in = Input(UInt(32.W))
        val instr_out = Output(UInt(32.W))

        val rd_in = Input(UInt(32.W))
        val rd_out = Output(UInt(32.W))

        val mem_write_in =  Input(Bool())
        val mem_write_out =  Output(Bool())

        val mem_read_in =  Input(Bool())
        val mem_read_out =  Output(Bool())

        val reg_write_in =  Input(Bool())
        val reg_write_out =  Output(Bool())

        val mem_to_reg_in =  Input(Bool())
        val mem_to_reg_out =  Output(Bool())

        val extend_sel_in =  Input(Bool())
        val extend_sel_out =  Output(Bool()) 
    })
    io.rs1_value_out := RegNext(io.rs1_value_in)
    io.rs2_value_out := RegNext(io.rs2_value_in)
    io.im_out := RegNext(io.im_in)
    io.instr_out := RegNext(io.instr_in)
    io.rd_out := RegNext(io.rd_in)

    io.rs1_out := RegNext(io.rs1_in)
    io.rs2_out := RegNext(io.rs2_in)
    io.extend_sel_out := RegNext(io.extend_sel_in)

    io.mem_write_out := RegNext(io.mem_write_in)
    io.mem_read_out := RegNext(io.mem_read_in)
    io.reg_write_out := RegNext(io.reg_write_in)
    io.mem_to_reg_out := RegNext(io.mem_to_reg_in)
    
}