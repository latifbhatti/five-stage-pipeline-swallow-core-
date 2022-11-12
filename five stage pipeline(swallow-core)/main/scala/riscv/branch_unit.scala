package riscv
import chisel3 . _
import chisel3 . util . _
class branch_unit extends Module {
  val io = IO(new Bundle {
    val rs1 = Input(SInt(32.W))
    val rs2 = Input(SInt(32.W))
    val fn3 = Input(UInt(3.W))
    val out = Output(Bool())
    val rs1_out = Output(SInt(32.W))
  })

  when(io.fn3 === "b000".U) {
    // BEQ
    when(io.rs1 === io.rs2) {
      io.out := 1.B
    } .otherwise {
      io.out := 0.B
    }
  } .elsewhen(io.fn3 === "b001".U) {
    // BNE
    when(io.rs1 =/= io.rs2) {
      io.out := 1.B
    } .otherwise {
      io.out := 0.B
    }
  } .elsewhen(io.fn3 === "b100".U) {
    // BLT
    when(io.rs1 < io.rs2) {
      io.out := 1.B
    } .otherwise {
      io.out := 0.B
    }
  } .elsewhen(io.fn3 === "b101".U) {
    // BGE
    when(io.rs1 >= io.rs2) {
      io.out := 1.B
    } .otherwise {
      io.out := 0.B
    }
  } .elsewhen(io.fn3 === "b110".U) {
    // BLTU
    when(io.rs1.asUInt < io.rs2.asUInt) {
      io.out := 1.B
    } .otherwise {
      io.out := 0.B
    }
  } .elsewhen(io.fn3 === "b111".U) {
    // BGEU
    when(io.rs1.asUInt >= io.rs2.asUInt) {
      io.out := 1.B
    } .otherwise {
      io.out := 0.B
    }
  } .otherwise {
    io.out := 0.B
  }
  io.rs1_out := io.rs1.asSInt
}