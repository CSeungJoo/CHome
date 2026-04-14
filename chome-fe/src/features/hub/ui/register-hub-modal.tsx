"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { AxiosError } from "axios";
import { Button, Input, Modal } from "@/shared/ui";
import { useRegisterHub } from "../hooks";

const schema = z.object({
  serialNumber: z.string().min(1, "시리얼 번호를 입력해주세요"),
  alias: z.string().min(1, "별명을 입력해주세요"),
});

type FormData = z.infer<typeof schema>;

function getErrorMessage(error: unknown): string {
  if (error instanceof AxiosError && error.response?.data?.data?.message) {
    return error.response.data.data.message;
  }
  return "허브 등록에 실패했습니다. 다시 시도해주세요.";
}

interface Props {
  isOpen: boolean;
  onClose: () => void;
}

export function RegisterHubModal({ isOpen, onClose }: Props) {
  const mutation = useRegisterHub();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const onSubmit = (data: FormData) => {
    mutation.mutate(data, {
      onSuccess: () => {
        reset();
        onClose();
      },
    });
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="허브 등록">
      {mutation.isError && (
        <div className="mb-4 p-3 bg-red-900/50 border border-red-700 rounded-lg text-sm text-red-400">
          {getErrorMessage(mutation.error)}
        </div>
      )}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="시리얼 번호"
          placeholder="허브 시리얼 번호"
          error={errors.serialNumber?.message}
          {...register("serialNumber")}
        />
        <Input
          label="별명"
          placeholder="예: 거실 허브"
          error={errors.alias?.message}
          {...register("alias")}
        />
        <div className="flex gap-3 justify-end">
          <Button type="button" variant="secondary" onClick={onClose}>
            취소
          </Button>
          <Button type="submit" loading={mutation.isPending}>
            등록
          </Button>
        </div>
      </form>
    </Modal>
  );
}
