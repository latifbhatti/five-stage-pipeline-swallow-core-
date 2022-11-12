package riscv
import chisel3 . _
import chisel3 . util . _
class register_file extends Module {
    val io = IO(new Bundle{
        val rs1_in = Input(UInt(5.W))
        val rs2_in = Input(UInt(5.W))
        val rd = Input(UInt(5.W))
        val rd_wb = Input(UInt(5.W))
        val reg_enable = Input(Bool())
        val reg_data = Input(SInt(32.W))

        val rs1_out = Output(SInt(32.W))
        val rs2_out = Output(SInt(32.W))
        val mem_to_wb_reg_write = Input(Bool())
    })
    val regs = RegInit(VecInit(Seq.fill(32)(0.S(32.W))))
    when((io.mem_to_wb_reg_write === 1.B) && (io.rs1_in === io.rd_wb )){
        val a = Mux((io.rs1_in === io.rd_wb),io.reg_data,0.S)
        val b =  Mux((io.rs2_in.orR),regs(io.rs2_in),0.S)
        io.rs1_out := a
        io.rs2_out := b
    }
    .elsewhen((io.mem_to_wb_reg_write === 1.B) && (io.rs2_in === io.rd_wb )){
        val a =  Mux((io.rs1_in.orR),regs(io.rs1_in),0.S)
        val b =  Mux((io.rs2_in === io.rd_wb),io.reg_data,0.S)
        io.rs1_out := a
        io.rs2_out := b
    }
    .otherwise{
        val a = Mux((io.rs1_in.orR),regs(io.rs1_in),0.S)
        val b =  Mux((io.rs2_in.orR),regs(io.rs2_in),0.S)
        io.rs1_out := a
        io.rs2_out := b
    }
    when (io.reg_enable & io.rd_wb.orR){
        regs(io.rd_wb):= io.reg_data
    }
}









































// import chisel3 . _
// import chisel3 . util . _
// class register_file extends Module {
//     val io = IO(new Bundle{
//         val instr = Input(UInt(32.W))
//         val reg_enable = Input(Bool())
//         val reg_data = Input(SInt(32.W))
//         val rs1_out = Output(SInt(32.W))
//         val rs2_out = Output(SInt(32.W))
//     })
//     val rs1_in=io.instr(19,15)
//     val rs2_in=io.instr(24,20)
//     val rd=io.instr(11,7)
//     val reg_memory = Mem(1024,SInt(32.W))
//     when(io.reg_enable){
//         reg_memory.write(rd,io.reg_data)
//     }
//     io.rs1_out := reg_memory.read(rs1_in)
//     io.rs2_out := reg_memory.read(rs2_in)
// }