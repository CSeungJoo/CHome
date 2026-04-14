"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button, Input, Modal } from "@/shared/ui";
import { useChangeHubAlias } from "../hooks";

const schema = z.object({
  alias: z.string().min(1, "별명을 입력해주세요"),
});

type FormData = z.infer<typeof schema>;

interface Props {
  isOpen: boolean;
  onClose: () => void;
  hubId: number;
  currentAlias: string;
}

export function EditHubAliasModal({ isOpen, onClose, hubId, currentAlias }: Props) {
  const mutation = useChangeHubAlias();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { alias: currentAlias },
  });

  const onSubmit = (data: FormData) => {
    mutation.mutate(
      { hubId, req: data },
      { onSuccess: () => onClose() }
    );
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="허브 별명 변경">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="새 별명"
          error={errors.alias?.message}
          {...register("alias")}
        />
        <div className="flex gap-3 justify-end">
          <Button type="button" variant="secondary" onClick={onClose}>
            취소
          </Button>
          <Button type="submit" loading={mutation.isPending}>
            변경
          </Button>
        </div>
      </form>
    </Modal>
  );
}
