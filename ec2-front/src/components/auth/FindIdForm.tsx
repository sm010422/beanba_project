import React, { useState } from 'react';
import { Mail } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useToast } from '@/hooks/use-toast';
import { authService } from '@/services/authService';

interface FindIdFormProps {
  onBack: () => void;
}

const FindIdForm = ({ onBack }: FindIdFormProps) => {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      await authService.findId(email);
      toast({
        title: "아이디 찾기 요청 완료",
        description: "가입시 이메일로 아이디를 발송했습니다.",
      });
      onBack();
    } catch (error) {
      toast({
        title: "오류",
        description: error instanceof Error ? error.message : "아이디 찾기에 실패했습니다.",
        variant: "destructive"
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="text-center text-sm text-gray-600">
        가입 시 사용한 이메일을 입력하시면<br />
        아이디를 이메일로 발송해드립니다.
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="email">이메일</Label>
          <div className="relative">
            <Mail className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
            <Input
              id="email"
              name="email"
              type="email"
              placeholder="이메일을 입력하세요"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="pl-10"
              required
            />
          </div>
        </div>

        <div className="space-y-2">
          <Button
            type="submit"
            className="w-full bg-green-600 hover:bg-green-700 text-white"
            disabled={isLoading}
          >
            {isLoading ? '처리 중...' : '아이디 찾기'}
          </Button>

          <Button
            type="button"
            variant="outline"
            className="w-full"
            onClick={onBack}
          >
            로그인으로 돌아가기
          </Button>
        </div>
      </form>
    </div>
  );
};

export default FindIdForm;