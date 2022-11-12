package riscv
import chisel3 . _
import chisel3 . util . _
class top_file extends Module {
    val io = IO(new Bundle{
    })
    val pc_module = Module(new pc_counter_plus_4)
    dontTouch(pc_module.io)
    val alu_control_module = Module(new alu_control)
    dontTouch(alu_control_module.io)
    val alu_module = Module(new alu)
    dontTouch(alu_module.io)
    val control_unit_module = Module(new control_unit)
    dontTouch(control_unit_module.io)
    val immediate_module = Module(new immediate_instruction)
    dontTouch(immediate_module.io)
    val instr_module = Module(new instr_memory)
    dontTouch(instr_module.io)
    val register_file_module = Module(new register_file)
    dontTouch(register_file_module.io)
    val s_memory_module = Module(new s_memory)
    dontTouch(s_memory_module.io)
    //five stage module
    val if_to_id_module = Module(new if_to_id)
    dontTouch(if_to_id_module.io)
    val id_to_ex_module = Module(new id_to_ex)
    dontTouch(id_to_ex_module.io)
    val ex_to_mem_module = Module(new ex_to_mem)
    dontTouch(ex_to_mem_module.io)
    val mem_to_wb_module = Module(new mem_to_wb)
    dontTouch(mem_to_wb_module.io)
    //forward module
    val forward_a_module = Module(new forward_a)
    dontTouch(forward_a_module.io)
    val forward_b_module = Module(new forward_b)
    dontTouch(forward_b_module.io)
    //hazard_detection_unit
    val detection_unit_module = Module(new hazard_detection_unit)
    dontTouch(detection_unit_module.io)
    val branch_unit_module = Module(new branch_unit)
    dontTouch(branch_unit_module.io)
    val branch_forward_a_module = Module(new branch_forward_a)
    dontTouch(branch_forward_a_module.io)
    val branch_forward_b_module = Module(new branch_forward_b)
    dontTouch(branch_forward_b_module.io)
    
    //-------------------------first pipeline start------------------------------------
    //instruction memory to instruction decoder (open)
    pc_module.io.pc_write := detection_unit_module.io.pc_write
    instr_module.io.address := pc_module.io.out >> 2.U
    
    if_to_id_module.io.pc_in := pc_module.io.out
    if_to_id_module.io.instr_in := instr_module.io.r_data
    if_to_id_module.io.if_id_write := detection_unit_module.io.if_id_write
    if_to_id_module.io.instr_h := detection_unit_module.io.instr_out
    if_to_id_module.io.if_id_branch := control_unit_module.io.branch_out
    //instruction memory to instruction decoder (close)
    
    //----------------------------------first pipeline end-------------------------------------

    //----------------------------------second pipeline start----------------------------------
    // connection between if/id pipeline to id/ex (input) pipieline start
    id_to_ex_module.io.rd_in := if_to_id_module.io.instr_out(11,7)
    id_to_ex_module.io.rs1_in := if_to_id_module.io.instr_out(19,15)
    id_to_ex_module.io.rs2_in := if_to_id_module.io.instr_out(24,20)
    id_to_ex_module.io.instr_in := if_to_id_module.io.instr_out
    id_to_ex_module.io.rs1_value_in := register_file_module.io.rs1_out
    id_to_ex_module.io.rs2_value_in := register_file_module.io.rs2_out
    id_to_ex_module.io.im_in := immediate_module.io.out
    id_to_ex_module.io.mem_write_in := control_unit_module.io.mem_write
    id_to_ex_module.io.mem_read_in := control_unit_module.io.mem_read
    id_to_ex_module.io.reg_write_in := control_unit_module.io.reg_write
    id_to_ex_module.io.mem_to_reg_in := control_unit_module.io.mem_to_reg
    id_to_ex_module.io.extend_sel_in := control_unit_module.io.extend_sel
    // connection between if/id pipeline to id/ex (input) pipieline end
    
    //immediate module start
    //define input of immediate module
    immediate_module.io.i_instruction := if_to_id_module.io.instr_out
    //immediate module complete
    
    //control unit start (main control unit to id/ix pipeline)
    //define input of control unit
    control_unit_module.io.op_code := if_to_id_module.io.instr_out(6,0)
    control_unit_module.io.control_unit_data_hazard := detection_unit_module.io.control_unit_data_hazard
    control_unit_module.io.branch_unit_branch := branch_unit_module.io.out
    //control unit complete (main control unit to id/ix pipeline)

    //hazard detection unit start
    //define input of hazard detection unit
    detection_unit_module.io.mem_read_id_ex := id_to_ex_module.io.mem_read_out
    detection_unit_module.io.instr := if_to_id_module.io.instr_out
    detection_unit_module.io.rd_id_ex := id_to_ex_module.io.rd_out
    //hazard detection unit close

    //write back mux is use in fifth pipeline
    val wb_mux = Mux(mem_to_wb_module.io.mem_to_reg_out,mem_to_wb_module.io.data_read_out,mem_to_wb_module.io.alu_result_out)
    val branch_end = branch_unit_module.io.out && control_unit_module.io.branch
    val jal = if_to_id_module.io.instr_out(6,0) === "b1101111".U
    val plus = if_to_id_module.io.pc_out + immediate_module.io.out.asUInt //<< 1.U
    val jalr = (branch_unit_module.io.rs1_out + immediate_module.io.out).asUInt
    val jalr_true = if_to_id_module.io.instr_out(6,0) === "b1100111".U
    when((branch_end === 1.B) || (jal === 1.B)){
        pc_module.io.pc := plus
    }
    .elsewhen(jalr_true === 1.B){
        pc_module.io.pc := jalr
    }
    .otherwise{
        pc_module.io.pc := pc_module.io.out4

    }
    //register file start
    //define (input) of refister file 
    register_file_module.io.rd := if_to_id_module.io.instr_out(11,7)
    register_file_module.io.rs1_in := if_to_id_module.io.instr_out(19,15)
    register_file_module.io.rs2_in := if_to_id_module.io.instr_out(24,20)
    register_file_module.io.mem_to_wb_reg_write := mem_to_wb_module.io.reg_write_out
    register_file_module.io.reg_data := wb_mux
    register_file_module.io.reg_enable := mem_to_wb_module.io.reg_write_out
    register_file_module.io.rd_wb := mem_to_wb_module.io.rd_out
    //register (input) file close

    // branch forward a (input) start
    branch_forward_a_module.io.rs1_in := if_to_id_module.io.instr_out(19,15)
    branch_forward_a_module.io.rd_id_ex := id_to_ex_module.io.rd_out
    branch_forward_a_module.io.rd_ex_mem := ex_to_mem_module.io.rd_out
    branch_forward_a_module.io.rd_mem_wb := mem_to_wb_module.io.rd_out
    branch_forward_a_module.io.reg_write_ex_mem := ex_to_mem_module.io.reg_write_out
    branch_forward_a_module.io.reg_write_mem_wb := mem_to_wb_module.io.reg_write_out
    branch_forward_a_module.io.reg_write_id_ex := id_to_ex_module.io.reg_write_out

    val branch_forward_a = MuxLookup(branch_forward_a_module.io.out,0.S,Array(
        (0.U) -> register_file_module.io.rs1_out,
        (1.U) -> wb_mux,
        (2.U) -> ex_to_mem_module.io.alu_result_out,
        (3.U) -> alu_module.io.out.asSInt)
    )
    dontTouch(branch_forward_a)
    // branch forward a (input) close

    // branch forward b (input) start
    branch_forward_b_module.io.rs2_in := if_to_id_module.io.instr_out(24,20)
    branch_forward_b_module.io.rd_id_ex := id_to_ex_module.io.rd_out
    branch_forward_b_module.io.rd_ex_mem := ex_to_mem_module.io.rd_out
    branch_forward_b_module.io.rd_mem_wb := mem_to_wb_module.io.rd_out
    branch_forward_b_module.io.reg_write_ex_mem := ex_to_mem_module.io.reg_write_out
    branch_forward_b_module.io.reg_write_mem_wb := mem_to_wb_module.io.reg_write_out
    branch_forward_b_module.io.reg_write_id_ex := id_to_ex_module.io.reg_write_out

    val branch_forward_b = MuxLookup(branch_forward_b_module.io.out,0.S,Array(
        (0.U) -> register_file_module.io.rs2_out,
        (1.U) -> wb_mux,
        (2.U) -> ex_to_mem_module.io.alu_result_out,
        (3.U) -> alu_module.io.out.asSInt)
    )
    dontTouch(branch_forward_b)
    // branch forward b (input) close

    //branch unit start
    //define (input) of branch unit
    branch_unit_module.io.fn3 := if_to_id_module.io.instr_out(14,12)
    branch_unit_module.io.rs1 := branch_forward_a
    branch_unit_module.io.rs2 := branch_forward_b
    //define (input) of branch unit close

    //-----------------------------------second pipeline end---------------------------------
    //-----------------------------------third pipeline start--------------------------------
    //forward unit start 
    //connection forward a (input) start
    forward_a_module.io.rs1_in := id_to_ex_module.io.rs1_out
    forward_a_module.io.rd_ex_mem := ex_to_mem_module.io.rd_out
    forward_a_module.io.rd_mem_wb := mem_to_wb_module.io.rd_out
    forward_a_module.io.reg_write_ex_mem := ex_to_mem_module.io.reg_write_out
    forward_a_module.io.reg_write_mem_wb := mem_to_wb_module.io.reg_write_out
    val forward_a = MuxLookup(forward_a_module.io.out,0.S,Array(
        (0.U) -> id_to_ex_module.io.rs1_value_out,
        (1.U) -> wb_mux,
        (2.U) -> ex_to_mem_module.io.alu_result_out)
    )
    //connection close a (input) end

    //connection forward b (input) start
    forward_b_module.io.rs2_in := id_to_ex_module.io.rs2_out
    forward_b_module.io.rd_ex_mem := ex_to_mem_module.io.rd_out
    forward_b_module.io.rd_mem_wb := mem_to_wb_module.io.rd_out
    forward_b_module.io.reg_write_ex_mem := ex_to_mem_module.io.reg_write_out
    forward_b_module.io.reg_write_mem_wb := mem_to_wb_module.io.reg_write_out
    val forward_b = MuxLookup(forward_b_module.io.out,0.S,Array(
        (0.U) -> id_to_ex_module.io.rs2_value_out,
        (1.U) -> wb_mux,
        (2.U) -> ex_to_mem_module.io.alu_result_out)
    )
    //connection close b (input) end
    //forward unit close

    // connection between id/ex pipeline to ex/mem (input) pipieline start
    ex_to_mem_module.io.mem_write_in := id_to_ex_module.io.mem_write_out
    ex_to_mem_module.io.mem_read_in := id_to_ex_module.io.mem_read_out
    ex_to_mem_module.io.reg_write_in := id_to_ex_module.io.reg_write_out
    ex_to_mem_module.io.mem_to_reg_in := id_to_ex_module.io.mem_to_reg_out
    ex_to_mem_module.io.rs2_value_in := forward_b
    ex_to_mem_module.io.alu_result_in := alu_module.io.out
    ex_to_mem_module.io.rd_in := id_to_ex_module.io.rd_out
    // connection between id/ex pipeline to ex/mem (input) pipieline end

    // alu module (input) start
    alu_module.io.a := forward_a
    alu_module.io.b := Mux(id_to_ex_module.io.extend_sel_out,id_to_ex_module.io.im_out,forward_b)
    alu_module.io.alu := alu_control_module.io.out
    // alu module (input) end

    //alu control (input) start
    alu_control_module.io.op_code := id_to_ex_module.io.instr_out(7,0)
    alu_control_module.io.fn3 := id_to_ex_module.io.instr_out(14,12)
    alu_control_module.io.fn7 := id_to_ex_module.io.instr_out(30)
    //alu control (input) end
    //----------------------------------third pipeline end--------------------------------------
    //----------------------------------forth pipeline start------------------------------------
    // connection between ex/mem pipeline to mem/wb (input) pipieline start
    mem_to_wb_module.io.mem_to_reg_in := ex_to_mem_module.io.mem_to_reg_out
    mem_to_wb_module.io.reg_write_in := ex_to_mem_module.io.reg_write_out
    mem_to_wb_module.io.data_read_in := s_memory_module.io.dataout
    mem_to_wb_module.io.alu_result_in := ex_to_mem_module.io.alu_result_out
    mem_to_wb_module.io.rd_in := ex_to_mem_module.io.rd_out
    // connection between ex/mem pipeline to mem/wb (input) pipieline start

    //data store memory(input) start
    s_memory_module.io.addr := ex_to_mem_module.io.alu_result_out.asUInt
    s_memory_module.io.mem_data := ex_to_mem_module.io.rs2_value_out
    s_memory_module.io.w_enable := ex_to_mem_module.io.mem_write_out
    s_memory_module.io.r_enable := ex_to_mem_module.io.mem_read_out
    //data store memory(input) end
    //----------------------------------forth pipeline end--------------------------------------
    //----------------------------------fifth pipeline start------------------------------------

    //***********************************define in second pipeline*****************************
    // ----------------------------------fifth pipeline end-------------------------------------
}