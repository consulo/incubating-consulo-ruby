module TEST
  @a
  @b
  @c
  @a
  @@a
  @@a
  @@b
  @@c
  A=1
  A=B
  C=1
    $A = 1
    $B = 2
    $C = 3
  class TEST
    def test
    end
  end

  module M
    def test
    end

    class C
      def test
      end
    end
  end

  def test
  end
end

class M::TEST1
  def test
  end
end

def test
end

alias f2 f1
alias f3 f2
alias f4 f3