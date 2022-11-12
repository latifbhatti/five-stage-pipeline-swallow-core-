package riscv
import chisel3 . _
import chisel3 . util . _
class hazard_detection_unit extends Module {
    val io = IO(new Bundle{
        val mem_read_id_ex = Input(Bool())
        val instr = Input(UInt(32.W))
        val rd_id_ex = Input(UInt(32.W))

        val if_id_write = Output(Bool())
        val pc_write = Output(Bool())
        val control_unit_data_hazard = Output(Bool())
        val instr_out =Output(UInt(32.W))
    })
    when((io.mem_read_id_ex === 1.B) && ((io.rd_id_ex === io.instr(19,15)) || (io.rd_id_ex === io.instr(24,20)))){
        io.if_id_write := 1.B
        io.pc_write := 1.B
        io.control_unit_data_hazard := 1.B
        io.instr_out := io.instr
    }
    .otherwise{
        io.if_id_write := 0.B
        io.pc_write := 0.B
        io.control_unit_data_hazard := 0.B
        io.instr_out := io.instr
    }
}