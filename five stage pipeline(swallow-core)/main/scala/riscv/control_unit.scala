package riscv
import chisel3 . _
import chisel3 . util . _
class control_unit(val on : Bool =1.B, val off : Bool =0.B) extends Module {
    val io =  IO(new Bundle{
        val op_code =  Input(UInt(7.W))
        val control_unit_data_hazard = Input(Bool())
        val mem_write =  Output(Bool())
        val branch =  Output(Bool())
        val branch_unit_branch = Input(Bool())
        val branch_out =  Output(Bool())

        val mem_read =  Output(Bool())
        val reg_write =  Output(Bool())
        val mem_to_reg =  Output(Bool())
        val extend_sel =  Output(Bool())
    })
    when(io.control_unit_data_hazard === 0.B){
        //r type
        when(io.op_code==="b0110011".U){
            io.mem_write := off
            io.branch := off
            io.mem_read := off
            io.reg_write := 1.B
            io.mem_to_reg := off
            io.extend_sel := 0.B

        }
        //i_type
        .elsewhen(io.op_code==="b0010011".U){
            io.mem_write := off
            io.branch := off
            io.mem_read := off
            io.reg_write := on
            io.mem_to_reg := off
            io.extend_sel := 1.B

        }
        
        //stype
        .elsewhen(io.op_code==="b0100011".U){
            io.mem_write :=on
            io.branch := off
            io.mem_read := off
            io.reg_write := off
            io.mem_to_reg := off
            io.extend_sel := 1.B

        }

        //b type
        .elsewhen(io.op_code==="b1100011".U){
            io.mem_write := off
            io.branch := on
            io.mem_read := off
            io.reg_write := off
            io.mem_to_reg := off
            io.extend_sel := 0.B

        }

        //u type
        .elsewhen(io.op_code==="b0110111".U){
            io.mem_write := off
            io.branch := off
            io.mem_read := off
            io.reg_write := on
            io.mem_to_reg := off
            io.extend_sel := 1.B

        }
        //j type
        .elsewhen(io.op_code==="b1101111".U){
            io.mem_write := off
            io.branch := off
            io.mem_read := off
            io.reg_write := on
            io.mem_to_reg := off
            io.extend_sel := 1.B

        }

        //jalr
        .elsewhen(io.op_code==="b1100111".U){
            io.mem_write := off
            io.branch := off
            io.mem_read := off
            io.reg_write := on
            io.mem_to_reg := off
            io.extend_sel := 0.B

        }

        //load
        .elsewhen(io.op_code==="b0000011".U){
            io.mem_write := off
            io.branch := off
            io.mem_read := on
            io.reg_write := on
            io.mem_to_reg := on
            io.extend_sel := 1.B

        }
        .otherwise{
            io.mem_write := 0.B
            io.branch := 0.B
            io.mem_read := 0.B
            io.reg_write := 0.B
            io.mem_to_reg := 0.B
            io.extend_sel := 0.B
        }
    }
    .otherwise{
        io.mem_write := 0.B
        io.branch := 0.B
        io.mem_read := 0.B
        io.reg_write := 0.B
        io.mem_to_reg := 0.B
        io.extend_sel := 0.B
    }
    io.branch_out := Mux((((io.op_code==="b1100011".U) && io.branch_unit_branch === 1.B) || (io.op_code==="b1101111".U) || (io.op_code==="b1100111".U)),1.B,0.B)
}