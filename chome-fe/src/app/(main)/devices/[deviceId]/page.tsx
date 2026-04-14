"use client";

import { use, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button, Card, Input, Modal } from "@/shared/ui";
import { useDeviceDetail, useChangeDeviceAlias } from "@/features/device/hooks";
import {
  HiOutlinePencil,
  HiOutlineCommandLine,
  HiOutlineCpuChip,
} from "react-icons/hi2";

const aliasSchema = z.object({
  alias: z.string().min(1, "별명을 입력해주세요"),
});

export default function DeviceDetailPage({
  params,
}: {
  params: Promise<{ deviceId: string }>;
}) {
  const { deviceId: deviceIdStr } = use(params);
  const deviceId = Number(deviceIdStr);

  const { data: device, isLoading } = useDeviceDetail(deviceId);
  const aliasMutation = useChangeDeviceAlias();
  const [showAliasModal, setShowAliasModal] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ resolver: zodResolver(aliasSchema) });

  const onAliasSubmit = (data: { alias: string }) => {
    aliasMutation.mutate(
      { deviceId, req: data },
      { onSuccess: () => setShowAliasModal(false) }
    );
  };

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full" />
      </div>
    );
  }

  if (!device) {
    return (
      <Card>
        <p className="text-center text-gray-500 py-8">
          디바이스를 찾을 수 없습니다
        </p>
      </Card>
    );
  }

  return (
    <div>
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 bg-green-600/20 rounded-lg flex items-center justify-center">
            <HiOutlineCpuChip className="w-6 h-6 text-green-400" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-gray-100">
              {device.alias || device.name}
            </h1>
            <p className="text-sm text-gray-500">{device.type}</p>
          </div>
        </div>
        <Button
          variant="secondary"
          size="sm"
          onClick={() => setShowAliasModal(true)}
        >
          <HiOutlinePencil className="w-4 h-4 mr-1" />
          별명 변경
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <h2 className="text-lg font-semibold text-gray-100 mb-4">기본 정보</h2>
          <dl className="space-y-3">
            <div className="flex justify-between">
              <dt className="text-sm text-gray-500">ID</dt>
              <dd className="text-sm font-medium text-gray-200">{device.id}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-sm text-gray-500">시리얼 번호</dt>
              <dd className="text-sm font-medium text-gray-200">
                {device.serialNumber}
              </dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-sm text-gray-500">이름</dt>
              <dd className="text-sm font-medium text-gray-200">{device.name}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-sm text-gray-500">타입</dt>
              <dd className="text-sm font-medium text-gray-200">{device.type}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-sm text-gray-500">별명</dt>
              <dd className="text-sm font-medium text-gray-200">
                {device.alias || "-"}
              </dd>
            </div>
          </dl>
        </Card>

        <Card>
          <h2 className="text-lg font-semibold text-gray-100 mb-4">
            <HiOutlineCommandLine className="w-5 h-5 inline mr-2" />
            사용 가능한 명령어
          </h2>
          {device.commands.length === 0 ? (
            <p className="text-sm text-gray-500">사용 가능한 명령어가 없습니다</p>
          ) : (
            <div className="space-y-2">
              {device.commands.map((cmd) => (
                <div
                  key={cmd.id}
                  className="flex items-center justify-between p-3 bg-gray-800 rounded-lg"
                >
                  <div>
                    <p className="text-sm font-medium text-gray-200">
                      {cmd.command}
                    </p>
                    <p className="text-xs text-gray-500">{cmd.description}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>

      <Modal
        isOpen={showAliasModal}
        onClose={() => setShowAliasModal(false)}
        title="디바이스 별명 변경"
      >
        <form onSubmit={handleSubmit(onAliasSubmit)} className="space-y-4">
          <Input
            label="새 별명"
            defaultValue={device.alias}
            error={errors.alias?.message}
            {...register("alias")}
          />
          <div className="flex gap-3 justify-end">
            <Button
              type="button"
              variant="secondary"
              onClick={() => setShowAliasModal(false)}
            >
              취소
            </Button>
            <Button type="submit" loading={aliasMutation.isPending}>
              변경
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
