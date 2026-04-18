"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { AxiosError } from "axios";
import { Button, Input, Modal } from "@/shared/ui";
import { useInviteHub } from "../hooks";
import { HubAction } from "../types";

const schema = z.object({
  targetEmail: z.string().email("올바른 이메일 주소를 입력해주세요"),
  permissions: z.array(z.nativeEnum(HubAction)).min(1, "최소 하나의 권한을 선택해주세요"),
});

type FormData = z.infer<typeof schema>;

function getErrorMessage(error: unknown): string {
  if (error instanceof AxiosError && error.response?.data?.data?.message) {
    return error.response.data.data.message;
  }
  return "초대에 실패했습니다. 다시 시도해주세요.";
}

interface Props {
  isOpen: boolean;
  onClose: () => void;
  hubId: number;
  hubAlias: string;
}

export function InviteHubModal({ isOpen, onClose, hubId, hubAlias }: Props) {
  const mutation = useInviteHub();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormData>({ 
    resolver: zodResolver(schema),
    defaultValues: {
      permissions: [HubAction.READ]
    }
  });

  const onSubmit = (data: FormData) => {
    mutation.mutate({ hubId, req: data }, {
      onSuccess: () => {
        alert(`${data.targetEmail}님을 ${hubAlias}에 초대했습니다.`);
        reset();
        onClose();
      },
    });
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`${hubAlias}에 초대`}>
      {mutation.isError && (
        <div className="mb-4 p-3 bg-red-900/50 border border-red-700 rounded-lg text-sm text-red-400">
          {getErrorMessage(mutation.error)}
        </div>
      )}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="이메일 주소"
          placeholder="초대할 사용자의 이메일"
          error={errors.targetEmail?.message}
          {...register("targetEmail")}
        />
        
        <div className="space-y-2">
          <label className="text-sm font-medium text-gray-300">권한 설정</label>
          <div className="grid grid-cols-2 gap-2">
            {Object.values(HubAction).map((action) => (
              <label key={action} className="flex items-center space-x-2 p-2 rounded bg-gray-800 border border-gray-700 cursor-pointer hover:bg-gray-700 transition-colors">
                <input
                  type="checkbox"
                  value={action}
                  className="w-4 h-4 rounded border-gray-600 bg-gray-700 text-blue-600 focus:ring-blue-500"
                  {...register("permissions")}
                />
                <span className="text-sm text-gray-200">{action}</span>
              </label>
            ))}
          </div>
          {errors.permissions?.message && (
            <p className="text-xs text-red-400 mt-1">{errors.permissions.message}</p>
          )}
        </div>

        <div className="flex gap-3 justify-end pt-4">
          <Button type="button" variant="secondary" onClick={onClose}>
            취소
          </Button>
          <Button type="submit" loading={mutation.isPending}>
            초대하기
          </Button>
        </div>
      </form>
    </Modal>
  );
}
