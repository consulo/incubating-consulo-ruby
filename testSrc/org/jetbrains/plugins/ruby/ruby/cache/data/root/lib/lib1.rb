$A = 1
$B = 2
module M1
  class M1_C1
    def M1_C1_f1
    end

    def M1_C1_f2
    end
  end

  def M1_f1
  end

  module M1_M2
    def M1_M2_f1
    end

    class M1_M1_C1
      def M1_M1_C1_f1
      end
    end
  end

  def M1_f1
  end
  $A = 3
  $C = 4
  $D
  def M1::f1
  end
end

class M2::C1
  def M2_C1_f1
  end
end

def f1
end

def f
end

alias f2 f1
alias f3 f2
alias f4 f3