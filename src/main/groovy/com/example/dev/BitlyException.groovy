package com.example.dev

class BitlyException extends Exception {
  def BitlyException(msg) {
    super("${msg}")
  }
}
