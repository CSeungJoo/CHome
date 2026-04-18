"use client";

import { useState } from "react";
import { Button, Card } from "@/shared/ui";
import { useHubs, useDeleteHub } from "@/features/hub/hooks";
import { RegisterHubModal, EditHubAliasModal, InviteHubModal } from "@/features/hub/ui";
import Link from "next/link";
import { HiOutlinePlus, HiOutlinePencil, HiOutlineTrash, HiOutlineUserPlus } from "react-icons/hi2";

import type { AccessibleHub } from "@/features/hub/types";

export default function HubsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useHubs(page);
  const deleteMutation = useDeleteHub();

  const [showRegister, setShowRegister] = useState(false);
  const [editHub, setEditHub] = useState<AccessibleHub | null>(null);
  const [inviteHub, setInviteHub] = useState<AccessibleHub | null>(null);

  const handleDelete = (hubId: number) => {
    if (confirm("정말 이 허브를 삭제하시겠습니까?")) {
      deleteMutation.mutate(hubId);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-100">허브 관리</h1>
        <Button onClick={() => setShowRegister(true)}>
          <HiOutlinePlus className="w-4 h-4 mr-1" />
          허브 등록
        </Button>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full" />
        </div>
      ) : data?.accessibleHubs.length === 0 ? (
        <Card>
          <div className="text-center py-12">
            <p className="text-gray-500 mb-4">등록된 허브가 없습니다</p>
            <Button onClick={() => setShowRegister(true)}>
              <HiOutlinePlus className="w-4 h-4 mr-1" />
              첫 허브 등록하기
            </Button>
          </div>
        </Card>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data?.accessibleHubs.map((hub) => (
              <Card key={hub.id}>
                <div className="flex items-start justify-between mb-3">
                  <Link href={`/hubs/${hub.id}`} className="hover:text-blue-400">
                    <h3 className="font-semibold text-gray-100">{hub.alias}</h3>
                  </Link>
                  {hub.isOwner && (
                    <span className="text-xs bg-blue-600/20 text-blue-400 px-2 py-1 rounded-full">
                      소유자
                    </span>
                  )}
                </div>
                <p className="text-sm text-gray-500 mb-4">
                  S/N: {hub.serialNumber}
                </p>
                <div className="flex flex-wrap gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setEditHub(hub)}
                  >
                    <HiOutlinePencil className="w-4 h-4 mr-1" />
                    별명 변경
                  </Button>
                  {hub.isOwner && (
                    <>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setInviteHub(hub)}
                        className="text-blue-400 hover:bg-blue-900/30"
                      >
                        <HiOutlineUserPlus className="w-4 h-4 mr-1" />
                        초대
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDelete(hub.id)}
                        className="text-red-400 hover:bg-red-900/30"
                      >
                        <HiOutlineTrash className="w-4 h-4 mr-1" />
                        삭제
                      </Button>
                    </>
                  )}
                </div>
              </Card>
            ))}
          </div>

          {data && data.totalCount > data.size && (
            <div className="flex justify-center gap-2 mt-6">
              <Button
                variant="secondary"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                이전
              </Button>
              <span className="flex items-center text-sm text-gray-400 px-4">
                {page + 1} / {Math.ceil(data.totalCount / data.size)}
              </span>
              <Button
                variant="secondary"
                size="sm"
                disabled={!data.hasNext}
                onClick={() => setPage((p) => p + 1)}
              >
                다음
              </Button>
            </div>
          )}
        </>
      )}

      <RegisterHubModal
        isOpen={showRegister}
        onClose={() => setShowRegister(false)}
      />

      {editHub && (
        <EditHubAliasModal
          isOpen={!!editHub}
          onClose={() => setEditHub(null)}
          hubId={editHub.id}
          currentAlias={editHub.alias}
        />
      )}

      {inviteHub && (
        <InviteHubModal
          isOpen={!!inviteHub}
          onClose={() => setInviteHub(null)}
          hubId={inviteHub.id}
          hubAlias={inviteHub.alias}
        />
      )}
    </div>
  );
}
