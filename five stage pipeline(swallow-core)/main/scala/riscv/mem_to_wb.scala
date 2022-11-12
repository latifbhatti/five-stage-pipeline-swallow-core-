package riscv
import chisel3 . _
import chisel3 . util . _
class mem_to_wb extends Module {
    val io =  IO(new Bundle{
        val data_read_in = Input(SInt(32.W))
        val data_read_out = Output(SInt(32.W))

        val alu_result_in = Input(SInt(32.W))
        val alu_result_out = Output(SInt(32.W))

        val rd_in = Input(UInt(32.W))
        val rd_out = Output(UInt(32.W))

        val reg_write_in =  Input(Bool())
        val reg_write_out =  Output(Bool())

        val mem_to_reg_in =  Input(Bool())
        val mem_to_reg_out =  Output(Bool())
    })
    io.data_read_out := RegNext(io.data_read_in)
    io.alu_result_out := RegNext(io.alu_result_in)
    io.rd_out := RegNext(io.rd_in)

    io.reg_write_out := RegNext(io.reg_write_in)
    io.mem_to_reg_out := RegNext(io.mem_to_reg_in)
}