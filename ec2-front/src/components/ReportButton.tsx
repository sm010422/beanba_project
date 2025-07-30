
import React, { useState } from 'react';
import { Flag } from 'lucide-react';
import { Button } from '@/components/ui/button';
import ReportModal from './ReportModal';

interface ReportButtonProps {
  postId: number;
  reporteePk: number;
  targetName?: string;
  className?: string;
}

const ReportButton = ({ postId, reporteePk, targetName, className = '' }: ReportButtonProps) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleReport = (e: React.MouseEvent) => {
    e.stopPropagation(); // 이벤트 버블링 방지
    setIsModalOpen(true);
  };

  return (
    <>
      <Button
        variant="ghost"
        size="sm"
        onClick={handleReport}
        className={`text-gray-500 hover:text-red-500 p-1 h-auto ${className}`}
        title="신고하기"
      >
        <Flag className="h-4 w-4" />
      </Button>

      <ReportModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        postPk={postId}
        reporteePk={reporteePk}
        targetName={targetName}
      />
    </>
  );
};

export default ReportButton;
